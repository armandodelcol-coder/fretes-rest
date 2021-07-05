package br.com.zupacademy.armando.controllers

import br.com.zupacademy.armando.CalculaFreteRequest
import br.com.zupacademy.armando.ErrorDetails
import br.com.zupacademy.armando.FretesServiceGrpc
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException

@Controller
class CalculaFretesController(val grpcClient: FretesServiceGrpc.FretesServiceBlockingStub) {
    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): FreteResponse {
        val request = CalculaFreteRequest.newBuilder()
            .setCep(cep)
            .build()
        try {
            val response = grpcClient.calculaFrete(request)
            return FreteResponse(cep = response.cep, valor = response.valor)
        } catch (e: StatusRuntimeException) {
            val statusCode = e.status.code
            val description = e.status.description
            if (statusCode == Status.Code.INVALID_ARGUMENT) {
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            if (statusCode == Status.Code.PERMISSION_DENIED) {
                val statusProto =
                    StatusProto.fromThrowable(e) ?: throw HttpStatusException(HttpStatus.FORBIDDEN, description)

                val anyDetails = statusProto.detailsList[0]
                val errorDetails = anyDetails.unpack(ErrorDetails::class.java)
                throw HttpStatusException(HttpStatus.FORBIDDEN, "${errorDetails.code}: ${errorDetails.message}")
            }

            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}

data class FreteResponse(val cep: String, val valor: Double) {

}
