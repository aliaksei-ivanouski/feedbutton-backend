package com.fetocan.feedbutton.service.jooq.bindings;

import com.vladmihalcea.hibernate.type.basic.Inet;
import org.jetbrains.annotations.NotNull;
import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Objects;

public class InetBinding implements Binding<Object, Inet> {
    @Override
    public @NotNull Converter<Object, Inet> converter() {
        return new Converter<Object, Inet>() {
            @Override
            public Inet from(Object databaseObject) {
                return databaseObject == null ? null : new Inet(databaseObject.toString());
            }

            @Override
            public Object to(Inet userObject) {
                return userObject == null ? null : userObject.getAddress();
            }

            @Override
            public @NotNull Class<Object> fromType() {
                return Object.class;
            }

            @Override
            public @NotNull Class<Inet> toType() {
                return Inet.class;
            }
        };
    }

    @Override
    public void sql(BindingSQLContext<Inet> ctx) throws SQLException {
        ctx.render().visit(DSL.val(ctx.convert(converter()).value())).sql("::inet");
    }

    @Override
    public void register(BindingRegisterContext<Inet> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    @Override
    public void set(BindingSetStatementContext<Inet> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
    }

    @Override
    public void set(BindingSetSQLOutputContext<Inet> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetResultSetContext<Inet> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
    }

    @Override
    public void get(BindingGetStatementContext<Inet> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
    }

    @Override
    public void get(BindingGetSQLInputContext<Inet> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
