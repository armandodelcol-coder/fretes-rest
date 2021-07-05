package br.com.zupacademy.armando.controllers

import br.com.zupacademy.armando.CalculaFreteRequest
import br.com.zupacademy.armando.FretesServiceGrpc
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue

@Controller
class CalculaFretesController(val grpcClient: FretesServiceGrpc.FretesServiceBlockingStub) {
    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): FreteResponse {
        val request = CalculaFreteRequest.newBuilder()
            .setCep(cep)
            .build()
        val response = grpcClient.calculaFrete(request)
        return FreteResponse(cep = response.cep, valor = response.valor)
    }
}

data class FreteResponse(val cep: String, val valor: Double) {

}
