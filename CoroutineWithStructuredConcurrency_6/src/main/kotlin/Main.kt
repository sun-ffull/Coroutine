package org.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*자식 코루틴을 기다리다 예외가 발생하면 예외가 부모로 전파되고, 다른 자식 코루틴에게 취소 요청을 보냄
* 이렇게 부모 자식이 한몸처럼 움직이는 것을 structured concurrency라고 함.
* 이는 수많은 코루틴이 유실되거나 누수되지 않도록 보장함.
* 이는 코드 내의 에러가 유실되지 않고 적절히 보고될 수 있도록 보장함.*/
fun main(): Unit = runBlocking{
    launch {
        delay(700)
        printWithThread("A")
    }

    launch {
        delay(600)
        throw IllegalArgumentException("코루틴 실패!")
    }

}

fun printWithThread(str: Any){
    println("[${Thread.currentThread().name}] $str")
}

/*++ 부모 코루틴이 취소되면, 자식 코루틴들이 취소됨.
* 단, CancellationException은 정상적인 취소로 간주하기 때문에 부모 코루틴에게 전파되지 않고,
* 부모 코루틴의 다른 자식 코루틴을 취소시키지도 않는다.*/