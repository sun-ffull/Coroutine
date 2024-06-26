package org.example
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main():Unit = runBlocking {
    val time = measureTimeMillis {
        //val job1 = async { apiCall1() }
        //val job2 = async { apiCall2() }
        //printWithThread(job1.await() + job2.await())

        //val job1 = async { apiCall1() }
        //val job2 = async { apiCall2(job1.await()) }
        //printWithThread(job2.await())

        /*CoroutineStart.LAZY 옵션 사용하면, await()함수를 호출했을 때 계산 결과를 계속 기다림.
        * 아래에서는 job1.await()호출 됐을 때 job1 시작. 결과를 job1.await()에 반환함.
        * 그다음에 job2.await()호출 됐을 때 job2 시작. 이하 동일
        * await() 전에 start()하면 상관없음.*/
        val job1 = async(start = CoroutineStart.LAZY){ apiCall1() }
        val job2 = async(start = CoroutineStart.LAZY){ apiCall2() }
        printWithThread(job1.await() + job2.await())
    }

    printWithThread("소요시간 : $time")
}

suspend fun apiCall1(): Int{
    delay(1000)
    return 1
}
suspend fun apiCall2(num: Int = 0): Int{
    delay(1000)
    return num+2
}

fun example5():Unit = runBlocking {
    /*async : launch와 달리 await을 통해 결과를 반환할 수 있음.
    * 장점 : call back을 사용하지 않고 동기방식 사용 가능함.*/
    val job = async{
        3+5
    }

    val value = job.await()     //async 결과를 가져오는 함수
}

fun example4():Unit = runBlocking {
    val job1 = launch {
        delay(1000)
        printWithThread("job1")
    }
    /*job1이 끝날때 까지 대기한다는 의미*/
    job1.join()

    val job2 = launch{
        delay(1000)
        printWithThread("job2")
    }
}

fun example3(): Unit = runBlocking {
    val job = launch {
        (1..5).forEach{
            printWithThread(it)
            delay(500)
        }
    }

    delay(1000)
    job.cancel()
}

fun example2(): Unit = runBlocking{
    val job = launch(start = CoroutineStart.LAZY) {
        printWithThread("hello launch")
    }

    delay(2000)
    job.start()
}

fun example1(){
    /*runBlocking은 해당 블록이 끝날때까지 스레드를 홀드하므로, main에 바로 써줄 때 만드는 것이 좋음.
    * runBlocking은 코루틴 빌더임.*/
    runBlocking {
        printWithThread("start")
        /*launch는 코루틴 빌더임*/
        launch {
            delay(2000)
            printWithThread("launch end")
        }
    }

    printWithThread("end")
}

fun printWithThread(str: Any){
    println("[${Thread.currentThread().name}] $str")
}