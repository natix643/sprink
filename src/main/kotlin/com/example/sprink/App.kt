package com.example.sprink

import com.example.sprink.UserRole.ADMIN
import com.example.sprink.UserRole.USER
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.StringWriter
import javax.persistence.*
import javax.persistence.EnumType.STRING

@SpringBootApplication
class App {

    @Bean fun runner(userRepo: UserRepo) = CommandLineRunner {

        userRepo.saveAll(listOf(
                User(
                        username = "admin",
                        role = ADMIN
                ),
                User(
                        username = "johndoe",
                        fullName = "John Doe"
                )
        ))
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(App::class.java, *args)
}

@RestController
@RequestMapping("/api/users")
class UserController(val userRepo: UserRepo) {

    @GetMapping
    fun getUsers(): List<User> = userRepo.findAll()
}

@RestController
@RequestMapping("/web/user")
class UserWebController(val userRepo: UserRepo) {

    @GetMapping
    fun getUsersPage() = StringWriter().appendHTML().html {
        head {
            title {
                +"Users"
            }
            meta {
                charset = "utf-8"
            }
        }

        body {
            table {
                thead {
                    tr {
                        th { +"ID" }
                        th { +"Username" }
                        th { +"Full name" }
                        th { +"Role" }
                    }
                }

                tbody {
                    userRepo.findAll().forEach { user ->
                        tr {
                            td { +"${user.id}" }
                            td { +user.username }
                            td { +"${user.fullName}" }
                            td { +"${user.role}" }
                        }
                    }
                }
            }
        }
    }.toString()
}

interface UserRepo : JpaRepository<User, Long>

@Entity
class User(
        @Id @GeneratedValue
        val id: Long? = null,

        @Column(nullable = false)
        val username: String,

        var fullName: String? = null,

        @Column(nullable = false)
        @Enumerated(STRING)
        val role: UserRole = USER
)

enum class UserRole {
    ADMIN, USER
}

