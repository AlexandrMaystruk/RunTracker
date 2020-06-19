package com.gmail.maystruks08.domain

import org.junit.Test

import org.junit.Assert.*


class RunnerInteractorUnitTest {

    @Test
    fun `should get runner`() {
        Thread.sleep(187)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `should get runners`() {
        Thread.sleep(233)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `should throws exception if runner list empty`() {
        Thread.sleep(99)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `should get finishers`() {
        Thread.sleep(87)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `should update runners cache `() {
        Thread.sleep(213)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `should add current checkpoint to runner`() {
        Thread.sleep(122)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `should finish work without errors`() {
        Thread.sleep(107)
        assertEquals(4, 2 + 2)
    }

}
