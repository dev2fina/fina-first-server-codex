package net.fina.server.i18n.helper;

import net.fina.server.i18n.impl.DescriptionManager;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

public class DescriptionUserType implements UserType<Description> {
    @Override
    public int getSqlType() {
        return Types.BIGINT;
    }

    @Override
    public Class<Description> returnedClass() {
        return Description.class;
    }


    @Override
    public Description nullSafeGet(ResultSet resultSet, int i,
                                   SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws SQLException {
        Description description = null;
        long nameStrId = resultSet.getLong(i);

        if (nameStrId > 0) {
            description = DescriptionManager.getInstance().getDescription(nameStrId);
        } else {
            description = new Description();
        }

        return description;
    }


    @Override
    public boolean equals(Description arg0, Description arg1) throws HibernateException {
        return arg0 == null ? arg1 == null : arg0.equals(arg1);
    }

    @Override
    public int hashCode(Description arg0) throws HibernateException {
        return arg0.hashCode();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Description deepCopy(Description arg0) throws HibernateException {
        return arg0;
    }


    @Override
    public void nullSafeSet(PreparedStatement st, Description value, int index, SharedSessionContractImplementor si) throws HibernateException, SQLException {
        long nameStrId = -1;

        if (value != null) {
            Description description = value;

            nameStrId = description.getNameStrId();

            if (!description.isEnableFilter()) {
                for (Map.Entry<Long, String> desc : description.getDescriptions().entrySet()) {
                    if (desc.getValue() != null) {
                        if (nameStrId > 0) {
                            DescriptionManager.getInstance().updateSysString(nameStrId, desc.getKey(), desc.getValue());
                        } else {
                            nameStrId = DescriptionManager.getInstance().getNameStrId(desc.getValue(), desc.getKey());
                            description.setNameStrId(nameStrId);
                        }
                    }
                }
            }
            st.setLong(index, nameStrId);
        } else {
            st.setNull(index, Types.BIGINT);
        }

//        LongType.INSTANCE.nullSafeSet(st, nameStrId, index, si);
    }

    @Override
    public Description assemble(Serializable arg0, Object arg1) throws HibernateException {
        throw new RuntimeException("'assemble' not supported!");
    }

    @Override
    public Serializable disassemble(Description arg0) throws HibernateException {
        throw new RuntimeException("'disassemble' not supported!");
    }

    @Override
    public Description replace(Description arg0, Description arg1, Object arg2) throws HibernateException {
        return arg0;
    }

}
