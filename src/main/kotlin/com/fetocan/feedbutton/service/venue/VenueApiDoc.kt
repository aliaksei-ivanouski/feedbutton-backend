package com.fetocan.feedbutton.service.venue

import com.fetocan.feedbutton.service.exception.ErrorModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse

@Operation(
    description = """
        Find the venue by their id
        
        Model reference to response object: VenueBasicProjection
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
                                  "code": "error.venue.not.found",
                                  "status": 404,
                                  "path": "/v1/venues/600",
                                  "error": "NotFoundException",
                                  "message": "Venue with id: 600 not found"
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
                                  "path": "/v1/venues/600",
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
annotation class GetVenueDoc

@Parameter(
    name = "id",
    description = "The venue id from the database"
)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class VenueIdDoc

@Operation(
    description = """
        Find pageable list of venues
        
        Model reference to response object: PageVenueBasicProjection
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
                                  "path": "/v1/venues",
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
annotation class GetVenuesDoc

@Operation(
    description = """
        Find pageable list of venues with managers belong to them
        
        Model reference to response object: PageVenueFullProjection
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
                                  "path": "/v1/venues",
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
annotation class GetVenuesWithManagersDoc
