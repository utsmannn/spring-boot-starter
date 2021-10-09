package com.utsman.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SimpleController {

    @GetMapping("/ping")
    fun ping(): String {
        return "oke"
    }
}