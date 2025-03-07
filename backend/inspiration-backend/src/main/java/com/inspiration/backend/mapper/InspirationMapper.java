package com.inspiration.backend.mapper;

import com.inspiration.backend.entity.Inspiration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface InspirationMapper {
    void insert(Inspiration inspiration);
    Inspiration findById(@Param("id") Long id);
    List<Inspiration> findByUserId(@Param("userId") Long userId);
    List<Inspiration> findPublic(@Param("page") Integer page, @Param("pageSize") Integer pageSize);
    void update(Inspiration inspiration);
    void delete(@Param("id") Long id);
    void updateStats(@Param("id") Long id, @Param("likes") Integer likes, @Param("collections") Integer collections);
}