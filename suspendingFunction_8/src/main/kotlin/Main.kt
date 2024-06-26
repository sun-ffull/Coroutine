package org.example

import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture

/*suspend function : 코루틴이 중지 되었다가 재개 될 수 있는 지점*/
fun example1(): Unit = runBlocking {
    val result1 = call1()
    val result2 = call2(result1)

    printWithThread(result2)
}

suspend fun call1():Int {
    return CoroutineScope(Dispatchers.Default).async{
        Thread.sleep(1000)
        100
    }.await()
}

suspend fun call2(num :Int):Int {
    /*CompletableFuture : 또다른 비동기 라이브러리*/
    return CompletableFuture.supplyAsync {
        Thread.sleep(1000)
        num+2
    }.await()
}

interface AsyncCaller{
    suspend fun call()
}
/*이런식으로 인터페이스를 이용하여 특정 클래스에는 a 비동기 라이브러리, 다른 클래스에는 b 비동기 라이브러리 사용할 수 있음.*/
class AcyncCallerImpl: AsyncCaller {
    override suspend fun call(){
        TODO()
    }
}

/*코루틴 suspend fun 종류 :
* coroutineScope -> launch, async와는 달리 사용된 즉시 실행됨. 아래 실행 결과 : start - 30 - end
* withContext() -> coroutineScope와 동일한 기능. but context를 바꾸고 싶을 때 사용.
* withTimeout, withTimeoutOrNull -> coroutineScope와 동일한 기능. but 주어진 시간안에 새로 생긴 코루틴이 완료되어야함.
* 주어진 시간 안에 완료 못하면, withTimeout은 예외를 던지고, withTimeoutOrNull은 null 반환*/
fun main2(): Unit = runBlocking {
    printWithThread("start")
    printWithThread(calculateResult2())
    printWithThread("end")
}

suspend fun calculateResult(): Int = coroutineScope {
    val num1 = async {
        delay(1000)
        10
    }

    val num2 = async {
        delay(1000)
        20
    }

    num1.await() + num2.await()
}

/*withContext() -> coroutineScope와 동일한 기능. but context를 바꾸고 싶을 때 사용.*/
suspend fun calculateResult2(): Int = withContext(Dispatchers.Default) {
    val num1 = async {
        delay(1000)
        10
    }

    val num2 = async {
        delay(1000)
        20
    }

    num1.await() + num2.await()
}

/*withTimeout, withTimeoutOrNull -> coroutineScope와 동일한 기능. but 주어진 시간안에 새로 생긴 코루틴이 완료되어야함.
* 주어진 시간 안에 완료 못하면, withTimeout은 예외를 던지고, withTimeoutOrNull은 null 반환*/
fun main(): Unit = runBlocking {
    val result: Int? = withTimeoutOrNull(1000){
        delay(1500)
        10+20
    }

    printWithThread(result)
}

fun printWithThread(str: Any?){
    println("[${Thread.currentThread().name}] $str")
}