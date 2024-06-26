package org.example

import kotlinx.coroutines.delay

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    val service = UserService()
    println(service.findUser(1L, null))
}

interface Continuation{
    suspend fun resumeWith(data: Any?)
}

class UserService{
    private val userProfileRepository = UserProfileRepository()
    private val userImageRepository = UserImageRepository()

    private abstract class FindUserContinuation() : Continuation{
        var label = 0
        var profile: Profile? = null
        var image: Image? = null
    }

    suspend fun findUser(userId: Long, continuation: Continuation?): UserDto{
        val sm = continuation as? FindUserContinuation ?: object:FindUserContinuation(){

            override suspend fun resumeWith(data: Any?) {
                when(label){
                    0 -> {
                        profile = data as Profile
                        label = 1
                    }
                    1 -> {
                        image = data as Image
                        label = 2
                    }
                }

                findUser(userId, this)
            }
        }

        when(sm.label){
            0 -> {//0단계 - 초기 시작
                println("프로필을 가져오겠습니다.")
                userProfileRepository.findProfile(userId, sm)
            }
            1 -> {//1단계 - 1차 중단 후 재시작
                println("이미지를 가져오겠습니다.")
                userImageRepository.findImage(sm.profile!!, sm)
            }
        }
        return UserDto(sm.profile!!, sm.image!!)
        /*
        //0단계 - 초기 시작
        println("프로필을 가져오겠습니다.")
        val profile = userProfileRepository.findProfile(userId)

        //1단계 - 1차 중단 후 재시작
        println("이미지를 가져오겠습니다.")
        val image = userImageRepository.findImage(profile)

        //2단계 - 2차 중단 후 재시작
        return UserDto(profile, image)*/
    }
}

class UserProfileRepository{
    suspend fun findProfile(userId: Long, continuation: Continuation){
        delay(100L)
        continuation.resumeWith(Profile())
    }
}

class Profile

class UserImageRepository{
    suspend fun findImage(profile: Profile, continuation: Continuation){
        delay(100L)
        continuation.resumeWith(Image())
    }
}

class Image

class UserDto(
    profile: Profile,
    image: Image){

}