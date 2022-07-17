package com.fetocan.feedbutton.service.manager

import com.fetocan.feedbutton.service.exception.ErrorModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse

@Operation(
    description = """
        Find the manager by their id
                
        Model reference to response object: ManagerBasicProjection
    """,
    responses = [
        ApiResponse(
            description = "Success",
            responseCode = "200"
        ),
        ApiResponse(
            description = "Not found",
            responseCode = "404",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        implementation = ErrorModel::class
                    ),
                    examples = [
                        ExampleObject(
                            value = """
                                {
                                  "timestamp": "2022-07-05T12:59:49.111530Z",
                                  "code": "error.manager.not.found",
                                  "status": 404,
                                  "path": "/v1/managers/600",
                                  "error": "NotFoundException",
                                  "message": "Manager with id: 600 not found"
                                }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            description = "Server error",
            responseCode = "500",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        implementation = ErrorModel::class
                    ),
                    examples = [
                        ExampleObject(
                            value = """
                                {
                                  "timestamp": "2022-07-05T13:01:48.647466Z",
                                  "code": "error.internal.server",
                                  "status": 500,
                                  "path": "/v1/managers/600",
                                  "error": "InternalServerError",
                                  "message": "Some error message"
                                }
                            """
                        )
                    ]
                )
            ]
        )
    ]
)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class GetManagerDoc

@Parameter(
    name = "id",
    description = "The manager id from the database"
)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ManagerIdDoc

@Operation(
    description = """
        Find pageable list of managers
                
        Model reference to response object: PageManagerBasicProjection
    """,
    responses = [
        ApiResponse(
            description = "Success",
            responseCode = "200"
        ),
        ApiResponse(
            description = "Server error",
            responseCode = "500",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        implementation = ErrorModel::class
                    ),
                    examples = [
                        ExampleObject(
                            value = """
                                {
                                  "timestamp": "2022-07-05T13:01:48.647466Z",
                                  "code": "error.internal.server",
                                  "status": 500,
                                  "path": "/v1/managers",
                                  "error": "InternalServerError",
                                  "message": "Some error message"
                                }
                            """
                        )
                    ]
                )
            ]
        )
    ]
)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class GetManagersDoc

@Operation(
    description = """
        Find pageable list of managers with departments they belong
        
        Model reference to response object: PageManagerFullProjection
    """,
    responses = [
        ApiResponse(
            description = "Success",
            responseCode = "200"
        ),
        ApiResponse(
            description = "Server error",
            responseCode = "500",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        implementation = ErrorModel::class
                    ),
                    examples = [
                        ExampleObject(
                            value = """
                                {
                                  "timestamp": "2022-07-05T13:01:48.647466Z",
                                  "code": "error.internal.server",
                                  "status": 500,
                                  "path": "/v1/managers",
                                  "error": "InternalServerError",
                                  "message": "Some error message"
                                }
                            """
                        )
                    ]
                )
            ]
        )
    ]
)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class GetManagersWithVenuesDoc
