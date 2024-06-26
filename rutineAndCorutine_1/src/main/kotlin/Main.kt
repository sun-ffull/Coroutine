package org.example

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

/*코루틴 세계를 만드는 runBlocking, launch*/
fun main(): Unit = runBlocking {
    printWithThread("Start")
    launch{
        newRoutine()
    }
    /*지금 코루틴을 중단하고 다른 코루틴이 실행되도록 함.*/
    yield()
    printWithThread("End")
}

/*suspend : 다른 suspend function을 부를 수 있게 해줌.*/
suspend fun newRoutine(){
    /*새로운 루틴이 호출된 후 완전히 종료되기 전, 해당 루틴에서 사용했던 정보들을 보관하고 있어야 함.*/
    val num1 = 1
    val num2 = 2

    yield()
    printWithThread("${num1 + num2}")
}

fun printWithThread(str: Any){
    println("[${Thread.currentThread().name}] $str")
}

