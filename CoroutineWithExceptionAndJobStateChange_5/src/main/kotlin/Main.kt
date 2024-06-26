package org.example

import kotlinx.coroutines.*

/*runBlocking : root 코루틴.*/
fun example1():Unit = runBlocking{
    /*CoroutineScope : root 코루틴을 만드는 방법
    * Dispatchers.Default : 코루틴을 다른 스레드에 할당.*/
    val job1 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000L)
        printWithThread("job1")
    }

    val job2 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000L)
        printWithThread("Job2")
    }
}

fun example2():Unit = runBlocking {
    val job1 = CoroutineScope(Dispatchers.Default).async {
        throw IllegalArgumentException()
    }

    delay(1000L)
    /*아래의 코드를 입력해주지 않으면 실행해도 예외발생을 알려주지 않음.
    * job1이 main스레드가 아닌 다른 스레드에서 작동하기 때문임*/
    job1.await()
}

fun example3():Unit = runBlocking {
    val job1 = async {
        throw IllegalArgumentException()
    }

    delay(1000L)
    /*이경우는 아래의 코드 없어도 실행하면 예외발생 알려줌.
    * 같은 스레드에서 실행될 경우 자식 코루틴의 예외는 부모에게 전파되기 때문임.*/
    //job1.await()
}

fun example4():Unit = runBlocking {
    /*async에 SupervisorJob을 설정하면 부모 자식 코루틴 관계는 유지되지만, 작동은 example2와 동일하게 작동함.*/
    val job1 = async(SupervisorJob()) {
        throw IllegalArgumentException()
    }

    delay(1000L)
    job1.await()
}

fun example5():Unit = runBlocking {
    /*예외 다루는 방법 1 : try-catch-finally 활용하기*/
    val job1 = async{
        try{
            throw IllegalArgumentException()
        } catch(e: IllegalArgumentException){
            println("정상작동")
        }
    }
    job1.await()
}

fun main():Unit = runBlocking {
    /*예외 다루는 방법 2 : CoroutineExceptionHandler 활용하기
    * 예외 발생 이후 에러 로깅 / 에러 메시지 전송 등에 활용
    * coroutineContext : 코루틴 객체 , throwable : 발생한 예외*/
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        printWithThread("예외")
        //throw throwable
    }

    /*exceptionHandler 사용 시 주의점 : launch에만 적용 가능하고, 부모 코루틴이 있으면 동작하지 않음.*/
    val job = CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
        throw IllegalArgumentException()
    }

    delay(1000)
}

fun printWithThread(str: Any){
    println("[${Thread.currentThread().name}] $str")
}

/*코루틴 취소 예외 정리 : 발생한 예외가 CancellationException인 경우 취소로 간주하고 부모 코루틴에게 전파 안함
* 그 외의 다른 예외가 발생한 경우 실패로 간주하고 부모 코루틴에게 전파함.
* 주의 : 어느 예외든 간에 일단 내부적으로는 취소로 취급됨. 핵심은 부모에게 전파하냐 안하냐이다..*/