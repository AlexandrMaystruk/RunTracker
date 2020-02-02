package com.gmail.maystruks08.nfcruntracker.core.navigation;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.LinkedList;

import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.Replace;


public class AppNavigator implements Navigator {

    private final Activity activity;
    private final FragmentManager fragmentManager;
    private final int containerId;
    private LinkedList<String> localStackCopy;

    public AppNavigator(FragmentActivity activity, FragmentManager fragmentManager, int containerId) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    @Override
    public void applyCommands(Command[] commands) {
        fragmentManager.executePendingTransactions();
        copyStackToLocal();

        for (Command command : commands) {
            applyCommand(command);
        }
    }

    private void copyStackToLocal() {
        localStackCopy = new LinkedList<>();

        final int stackSize = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < stackSize; i++) {
            localStackCopy.add(fragmentManager.getBackStackEntryAt(i).getName());
        }
    }

    private void applyCommand(Command command) {
        if (command instanceof Forward) {
            fragmentForward((Forward) command);
        } else if (command instanceof Replace) {
            fragmentReplace((Replace) command);
        } else if (command instanceof BackTo) {
            backTo((BackTo) command);
        } else if (command instanceof Back) {
            fragmentBack();
        }
    }

    private void fragmentForward(Forward command) {
        AppScreen screen = (AppScreen) command.getScreen();
        Fragment fragment = createFragment(screen);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        setupFragmentTransaction(
                command,
                fragmentManager.findFragmentById(containerId),
                fragment,
                fragmentTransaction
        );

        fragmentTransaction
                .replace(containerId, fragment)
                .addToBackStack(screen.getScreenKey())
                .commit();
        localStackCopy.add(screen.getScreenKey());
    }

    private void fragmentBack() {
        if (localStackCopy.size() > 0) {
            fragmentManager.popBackStack();
            localStackCopy.removeLast();
        } else {
            activityBack();
        }
    }

    private void activityBack() {
        activity.finish();
    }

    private void fragmentReplace(Replace command) {
        AppScreen screen = (AppScreen) command.getScreen();
        Fragment fragment = createFragment(screen);

        if (localStackCopy.size() > 0) {
            fragmentManager.popBackStack();
            localStackCopy.removeLast();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            setupFragmentTransaction(
                    command,
                    fragmentManager.findFragmentById(containerId),
                    fragment,
                    fragmentTransaction
            );

            fragmentTransaction
                    .replace(containerId, fragment, screen.getScreenKey())
                    .addToBackStack(screen.getScreenKey())
                    .commit();
            localStackCopy.add(screen.getScreenKey());

        } else {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            setupFragmentTransaction(
                    command,
                    fragmentManager.findFragmentById(containerId),
                    fragment,
                    fragmentTransaction
            );

            fragmentTransaction
                    .replace(containerId, fragment, screen.getScreenKey())
                    .commit();
        }
    }

    private void backTo(BackTo command) {
        if (command.getScreen() == null) {
            backToRoot();
        } else {
            String key = command.getScreen().getScreenKey();
            int index = localStackCopy.indexOf(key);
            int size = localStackCopy.size();

            if (index != -1) {
                for (int i = 1; i < size - index; i++) {
                    localStackCopy.removeLast();
                }
                fragmentManager.popBackStack(key, 0);
            } else {
                backToUnexisting();
            }
        }
    }

    private void backToRoot() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        localStackCopy.clear();
    }

    protected void setupFragmentTransaction(Command command,
                                            Fragment currentFragment,
                                            Fragment nextFragment,
                                            FragmentTransaction fragmentTransaction) {
    }


    private Fragment createFragment(AppScreen screen) {
        Fragment fragment = screen.getFragment();

        if (fragment == null) {
            errorWhileCreatingScreen(screen);
        }
        return fragment;
    }


    private void backToUnexisting() {
        backToRoot();
    }

    private void errorWhileCreatingScreen(AppScreen screen) {
        throw new RuntimeException("Can't create a screen: " + screen.getScreenKey());
    }
}
