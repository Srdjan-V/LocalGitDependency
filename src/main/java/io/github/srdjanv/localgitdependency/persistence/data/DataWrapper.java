package io.github.srdjanv.localgitdependency.persistence.data;

public class DataWrapper {
    private final Object data;
    private final DataType dataType;

    @SuppressWarnings("rawtypes")
    public static DataWrapper create(DataLayout.DataMapper mapper, Object data) {
        return new DataWrapper(mapper, data);
    }

    @SuppressWarnings("rawtypes")
    public static DataWrapper create(DataLayout.DataMapper mapper) {
        return new DataWrapper(mapper, mapper.getInstanceSupplier().get());
    }

    @SuppressWarnings("rawtypes")
    private DataWrapper(DataLayout.DataMapper mapper, Object data) {
        this.data = data;
        dataType = mapper.getDataType();
    }

    public Object getData() {
        return data;
    }

    public DataType getDataType() {
        return dataType;
    }
}
