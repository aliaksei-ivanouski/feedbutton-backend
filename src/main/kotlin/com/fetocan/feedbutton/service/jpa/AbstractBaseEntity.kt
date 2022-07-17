package com.fetocan.feedbutton.service.jpa

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vladmihalcea.hibernate.type.array.ListArrayType
import com.vladmihalcea.hibernate.type.basic.Inet
import com.vladmihalcea.hibernate.type.basic.PostgreSQLInetType
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.io.Serializable
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.PostLoad
import javax.persistence.PostPersist

@TypeDefs(
    TypeDef(name = "json", typeClass = JsonStringType::class),
    TypeDef(name = "jsonb", typeClass = JsonBinaryType::class),
    TypeDef(name = "list_array", typeClass = ListArrayType::class),
    TypeDef(name = "inet", typeClass = PostgreSQLInetType::class, defaultForType = Inet::class)
)
@MappedSuperclass
abstract class AbstractBaseEntity(
    givenId: Long? = null
): AuditedEntity(), PersistableSerializable<Long>, Serializable {

    @Id
    private val id: Long = givenId ?: 0

    override fun getId(): Long = id

    @Transient
    private var persisted: Boolean = givenId != null

    @JsonIgnore
    override fun isNew(): Boolean = !persisted

    @PostPersist
    @PostLoad
    private fun setPersist() {
        persisted = true
    }

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null -> false
            other !is AbstractBaseEntity -> false
            else -> getId() == other.getId()
        }
    }
}
