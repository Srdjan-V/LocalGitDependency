package io.github.srdjanv.localgitdependency.persistence.data;

import io.github.srdjanv.localgitdependency.persistence.data.dependency.DependencyData;
import io.github.srdjanv.localgitdependency.persistence.data.probe.ProjectProbeData;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataLayout implements Comparator<Object> {
    private static final DataLayout dependencyLayout;

    public static DataLayout getDependencyLayout() {
        return dependencyLayout;
    }

    static {
        dependencyLayout = create(dataLayout -> {
            dataLayout.<DependencyData>registerDataMapper(dataMapper -> {
                dataMapper.setInstanceSupplier(DependencyData::new);
                dataMapper.setClazz(DependencyData.class);
                dataMapper.setDataType(DataType.DependencyData);
            });
            dataLayout.<ProjectProbeData>registerDataMapper(dataMapper -> {
                dataMapper.setInstanceSupplier(ProjectProbeData::new);
                dataMapper.setClazz(ProjectProbeData.class);
                dataMapper.setDataType(DataType.ProjectProbeData);
            });
        });
    }

    public static DataLayout create(Consumer<DataLayout> configurator) {
        DataLayout data = new DataLayout();
        configurator.accept(data);
        return data;
    }

    private final Set<DataMapper<?>> dataMappers = new HashSet<>();
    private int layoutIndex;

    private DataLayout() {
    }

    public <T> void registerDataMapper(Consumer<DataMapper<T>> dataMapper) {
        DataMapper<T> data = new DataMapper<>();
        dataMapper.accept(data);
        if (!dataMappers.add(data)) {
            throw new RuntimeException(String.format("Duplicate dataMapper: %s for DataLayout", data.getDataType().name()));
        }
    }

    public Set<DataMapper<?>> getDataMappers() {
        return dataMappers;
    }

    @Override
    public int compare(Object o1, Object o2) {
        Integer intO1 = null;
        Integer intO2 = null;

        for (DataMapper<?> dataMapper : dataMappers) {
            if (dataMapper.sameClass(o1)) {
                intO1 = dataMapper.getInstanceIndex();
                continue;
            }
            if (dataMapper.sameClass(o2)) {
                intO2 = dataMapper.getInstanceIndex();
            }
        }

        if (intO1 == null || intO2 == null) {
            throw new RuntimeException("Missing DataMapper");
        }

        return intO1 - intO2;
    }

    public class DataMapper<T> {
        private final int instanceIndex;
        private Supplier<T> instanceSupplier;
        private DataType dataType;
        private Class<T> clazz;

        public DataMapper() {
            this.instanceIndex = layoutIndex++;
        }

        private void setInstanceSupplier(Supplier<T> instanceSupplier) {
            this.instanceSupplier = instanceSupplier;
        }

        public void setDataType(DataType dataType) {
            this.dataType = dataType;
        }

        private void setClazz(Class<T> clazz) {
            this.clazz = clazz;
        }

        public boolean sameClass(Object o) {
            return this.clazz == o.getClass();
        }

        public int getInstanceIndex() {
            return instanceIndex;
        }

        public Supplier<T> getInstanceSupplier() {
            return instanceSupplier;
        }

        public DataType getDataType() {
            return dataType;
        }

        public Class<T> getClazz() {
            return clazz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataMapper<?> that = (DataMapper<?>) o;
            return dataType == that.dataType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(dataType);
        }
    }

}
