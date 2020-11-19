package com.gmail.maystruks08.nfcruntracker.ui.login

sealed class AuthState

object Google : AuthState()

class EmailAndPassword(val isRegisterNewUser: Boolean) : AuthState()

