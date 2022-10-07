package com.codebrew.routes

import com.codebrew.models.Customer
import com.codebrew.models.customerStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.customerRouting() {
    route("/customer") {
        get {
            if (customerStorage.isNotEmpty()) call.respond(customerStorage)
            else call.respondText("No customers Found", status = HttpStatusCode.OK)
        }
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing Id", status = HttpStatusCode.BadRequest
            )
            val customer = customerStorage.find {
                it.id == id
            } ?: return@get call.respondText(
                "No customer with id $id", status = HttpStatusCode.NotFound
            )
            call.respond(customer)
        }
        post {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respondText("Customer stored successfully", status = HttpStatusCode.Created)
        }
        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respondText(
                "Missing Id", status = HttpStatusCode.BadRequest
            )
            if (customerStorage.removeIf{it.id == id}) {
                call.respondText("Customer removed successfully", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}