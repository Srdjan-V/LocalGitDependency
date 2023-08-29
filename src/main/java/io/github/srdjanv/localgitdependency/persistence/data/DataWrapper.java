package io.github.srdjanv.localgitdependency.persistence.data;

public class DataWrapper {
    private final Object data;
    private final DataType dataType;
    private final boolean valid;

    @SuppressWarnings("rawtypes")
    public static DataWrapper create(DataLayout.DataMapper mapper, Object data) {
        return new DataWrapper(mapper, data, true);
    }

    @SuppressWarnings("rawtypes")
    public static DataWrapper create(DataLayout.DataMapper mapper) {
        return new DataWrapper(mapper, mapper.getInstanceSupplier().get(), false);
    }

    @SuppressWarnings("rawtypes")
    private DataWrapper(DataLayout.DataMapper mapper, Object data, boolean valid) {
        this.data = data;
        dataType = mapper.getDataType();
        this.valid = valid;
    }

    public Object getData() {
        return data;
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isValid() {
        return valid;
    }
}
