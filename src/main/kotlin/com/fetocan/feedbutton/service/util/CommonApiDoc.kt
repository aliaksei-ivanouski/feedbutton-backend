package com.fetocan.feedbutton.service.util

import io.swagger.v3.oas.annotations.Parameter

@Parameter(
    name = "pageable",
    description = """
        Pageable request
        
        Parameters description:
        - page - page number, starts from 0
        - size - page size
        - sort - array of comma divided sequential <field_name>,<sort_direction>
                 it works the same way as SQL "ORDER BY `name` ASC, `age` DESC"
                 
        Example:
        {
          "page": 2,
          "size": 5,
          "sort": [
            "name,asc",
            "age,desc"
          ]
        }
    """
)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class PageableDoc
