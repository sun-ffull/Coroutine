package org.example

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/*CoroutineScope : 코루틴이 탄생할 수 있는 영역
* launch, async 는 모두 CoroutineScope의 확장함수임. runBlocking은 CoroutineScope를 제공.
* 즉, 우리가 직접 CoroutineScope를 만들면 runBlocking은 필요하지 않음.*/
fun example1_1() {
    CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("Job1")
    }

    Thread.sleep(1500)
}

/*thread sleep 사용 안하고 하는 방법*/
suspend fun example1_2() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("Job1")
    }

    job.join()
}

/*CoroutineContext : 코루틴과 관련된 여러가지 데이터를 갖음. Map+Set을 합쳐놓은 형태
* 각 요소들을 + 기호를 활용해서 추가할 수 있으며, key를 이용해 제거하는 것도 가능. 더 궁금하면 검색ㄱㄱ
* ex) CoroutineExceptionHandler, CoroutineDispatcher, 코루틴 이름, 코루틴 그 자체
* Dispatcher : 코루틴이 어떤 스레드에 배정될지를 관리하는 역할.
* 종류->Dispatchers.Default : CPU자원을 많이 쓸 때 권장. 별다른 설정이 없으면 이 디스패처 사용됨
*      Dispatchers.IO : I/O 작업에 최적화됨
*      Dispatchers.Main : 보통 UI컴포넌트를 조작하기 위한 디스패처. 특정 의존성을 갖고 있어야 정상적으로 활용가능. 그냥은 못씀.
*      자바의 ExecutorService를 디스패처로 asCoroutineDispatcher()확장함수 활용.. --> 나중에 검색해서 활용. 예시는 아래 메인에*/
fun main(){
    val threadPool = Executors.newSingleThreadExecutor()
    CoroutineScope(threadPool.asCoroutineDispatcher()).launch {
        printWithThread("새로운 코루틴")
    }
}

/*클래스 내부에서 독립적인 CoroutineScope을 관리함으로싸 해당 클래스에서 사용하던 코루틴을 한번에 종료시킬 수 있다.*/
class AsyncLogic{
    private val scope = CoroutineScope(Dispatchers.Default)

    fun doSomething(){
        scope.launch {
            //무언가 코루틴이 시작되어 작업!
        }
    }

    /*해당 scope에서 돌고 있던 모든 코루틴 destroy*/
    fun destroy(){
        scope.cancel()
    }
}

fun printWithThread(str: Any){
    println("[${Thread.currentThread().name}] $str")
}

/*코루틴의 Structured Concurrency 기반
* --> 한 CoroutineScope내에서 부모 코루틴이 있으면, Context에는 부모 코루틴 그 자체, 이름, Dispatcher.Default가 존재
* 자식 코루틴을 만들면 부모 Context를 복사하고, 필요한 부분 덮어 씌움. Context에는 자식 코루틴 그 자체, 새 이름,
* Dispatcher.Default가 존재 하게됨. 이때 부모-자식 관계도 설정.*/