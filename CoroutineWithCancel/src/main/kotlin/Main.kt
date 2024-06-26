package org.example
import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException

/*코루틴에서 취소에 협조하는 방법1 : delay, yield 같은 suspend함수 사용*/
fun example1(): Unit = runBlocking {
    val job = launch{
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5){
            if (nextPrintTime <= System.currentTimeMillis()){
                printWithThread("${i++}번째 출력")
                nextPrintTime += 1000L
            }
        }
    }

    delay(100)
    job.cancel()
}

/*메인스레드*/
fun example2(): Unit = runBlocking {
    /*Dispatchers.Default : 우리의 코루틴을 다른 스레드에 배정*/
    val job = launch(Dispatchers.Default){
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5){
            if (nextPrintTime <= System.currentTimeMillis()){
                printWithThread("${i++}번째 출력")
                nextPrintTime += 1000L
            }

            /*isActive:현재 코루틴이 활성화 되어 있는지, 취소 신호를 받았는지 확인*/
            if (!isActive){
                throw CancellationException()
            }
        }
    }

    delay(100)
    job.cancel()
}

/*suspend 함수에서도 CancellationException을 던지고 있기 때문에 try-catch로 잡고 throw를 하지 않으면 취소되지 않음*/
fun main(): Unit = runBlocking {
    val job = launch {
        try{
            delay(1000)
        } catch (e: CancellationException){
            //throw e -> 해당 e를 강제로 발생시킴.
        }

        printWithThread("delay에 의해 취소되지 않음")
    }

    delay(100)
    printWithThread("취소 시작")
    job.cancel()
}

fun printWithThread(str: Any){
    println("[${Thread.currentThread().name}] $str")
}