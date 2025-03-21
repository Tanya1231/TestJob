package com.example.testjob

import retrofit2.Call
import retrofit2.http.GET

interface CourseApiService {
    @GET("u/0/uc?id=15arTK7XT2b7Yv4BJsmDctA4Hg-BbS8-q&export=download")
    fun getCourses(): Call<CoursesResponse>
}