package com.example.mobiledger.data

import com.example.mobiledger.common.utils.JsonUtils
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.sources.api.model.response.ErrorResponse
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.RetrofitResult
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException

object ErrorMapper {
    fun <T> checkAndMapError(response: Response<T>?, exception: Exception?): RetrofitResult<T> {

        return when {
            exception != null -> {
                mapExceptionToError(exception)
            }
            response != null -> {
                checkAndMapApiError(response)
            }
            else -> {
                RetrofitResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
        }
    }

    private fun <T> checkAndMapApiError(response: Response<T>): RetrofitResult<T> {
        return if (response.isSuccessful) {
            RetrofitResult.Success(response.body())
        } else {
            val errorResponse: ErrorResponse? = JsonUtils.convertJsonStringToObject<ErrorResponse>(
                response.errorBody()?.string()
            )
            RetrofitResult.Failure(
                mapErrorCode(
                    errorResponse?.statusCode ?: response.code(),
                    errorResponse?.message
                )
            )
        }
    }
}

private fun mapErrorCode(code: Int, message: String? = null): AppError {
    return when (code) {
        400 -> AppError(code = ErrorCodes.HTTP_BAD_REQUEST, message = message)
        401 -> AppError(code = ErrorCodes.HTTP_UNAUTHORIZED, message = message)
        404 -> AppError(code = ErrorCodes.HTTP_NOT_FOUND, message = message)
        408 -> AppError(code = ErrorCodes.HTTP_REQUEST_TIMEOUT, message = message)
        500 -> AppError(code = ErrorCodes.HTTP_SERVER_INTERNAL_ERROR, message = message)
        502 -> AppError(code = ErrorCodes.HTTP_BAD_GATEWAY, message = message)
        503 -> AppError(code = ErrorCodes.HTTP_SERVICE_UNAVAILABLE, message = message)
        504 -> AppError(code = ErrorCodes.HTTP_GATEWAY_TIMEOUT, message = message)
        else -> {
            AppError(ErrorCodes.GENERIC_ERROR)
        }
    }
}

private fun mapExceptionToError(exception: Exception?): RetrofitResult.Failure {
    val error = when (exception) {
        is HttpException -> {
            mapErrorCode(exception.code())
        }
        is ConnectException -> {
            AppError(ErrorCodes.OFFLINE)
        }
        else -> {
            AppError(ErrorCodes.GENERIC_ERROR)
        }
    }
    return RetrofitResult.Failure(error)
}