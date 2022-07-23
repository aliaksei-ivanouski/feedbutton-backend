package com.fetocan.feedbutton.service.manager

import com.fetocan.feedbutton.service.exception.ErrorModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse

@Operation(
    description = """
        Invite the manager to the system and assign the permissions to the manager
                
        Model reference to response object: CreateManagerPayload
    """,
    responses = [
        ApiResponse(
            description = "Success",
            responseCode = "200"
        ),
        ApiResponse(
            description = "Manager already has the access for some venue/s",
            responseCode = "400",
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
                                    "timestamp": "2022-07-23T14:06:02.928948Z",
                                    "code": "error.manager.venue.mapping.already.exists",
                                    "status": 400,
                                    "path": "/v1/venues/1/managers",
                                    "error": "BadRequestException",
                                    "message": "Venue mapping already exists for the given manager"
                                }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            description = "Access denied error",
            responseCode = "403",
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
                                  "timestamp": "2022-07-23T13:29:57.139333Z",
                                  "code": "error.access.denied",
                                  "status": 403,
                                  "path": "/v1/venues/1/managers",
                                  "error": "AccessDeniedException",
                                  "message": "Access is denied"
                                }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            description = "Venue not found",
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
                                    "timestamp": "2022-07-23T14:16:43.152612Z",
                                    "code": "error.venue.not.found",
                                    "status": 404,
                                    "path": "/v1/venues/20/managers",
                                    "error": "NotFoundException",
                                    "message": "Venue with id: 20 not found"
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
                                  "path": "/v1/venues/121/managers",
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
annotation class VenueManagerApiDocs
