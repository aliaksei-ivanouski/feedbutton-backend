package com.fetocan.feedbutton.service.jpa

import org.springframework.data.domain.Persistable
import java.io.Serializable

interface PersistableSerializable<ID> : Persistable<ID>, Serializable {

    override fun getId(): ID?

    override fun isNew(): Boolean
}
