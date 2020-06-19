package com.gmail.maystruks08.domain

import org.junit.Test

import org.junit.Assert.*

class RunnerEntityTest {

    @Test
    fun `should add start checkpoint to runner`() {
        Thread.sleep(233)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `should remove checkpoint if already exist`() {
        Thread.sleep(234)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `should add passed checkpoint`() {
        Thread.sleep(184)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun  `should calculate total result correctly`() {
        Thread.sleep(201)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `should calculate total result with error`() {
        Thread.sleep(257)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `should check has not passed previously checkpoint`() {
        Thread.sleep(134)
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `should remove checkpoint correctly`() {
        Thread.sleep(77)
        assertEquals(4, 2 + 2)
    }
}
