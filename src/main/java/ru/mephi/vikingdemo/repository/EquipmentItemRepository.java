package ru.mephi.vikingdemo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.mephi.vikingdemo.model.EquipmentItemEntity;

import java.util.List;

@Repository
public class EquipmentItemRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<EquipmentItemEntity> equipmentRowMapper = (rs, rowNum) ->
            new EquipmentItemEntity(
                    rs.getInt("id"),
                    rs.getInt("viking_id"),
                    rs.getString("name"),
                    rs.getString("quality")
            );

    public EquipmentItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EquipmentItemEntity> findByVikingId(long vikingId) {
        String sql = """
                select id, viking_id, name, quality
                from equipment_items
                where viking_id = ?
                order by id
                """;

        return jdbcTemplate.query(sql, equipmentRowMapper, vikingId);
    }

    public List<EquipmentItemEntity> findAll() {
        String sql = """
                select id, viking_id, name, quality
                from equipment_items
                order by viking_id, id
                """;

        return jdbcTemplate.query(sql, equipmentRowMapper);
    }

    public void save(EquipmentItemEntity item) {
        Integer vikingId = item.vikingId();
        String name = item.name();
        String quality = item.quality();
        String sql = """
                insert into equipment_items(viking_id, name, quality)
                values (?, ?, ?)
                """;

        jdbcTemplate.update(sql, vikingId, name, quality);
    }

    public void deleteByVikingId(long vikingId) {
        String sql = "delete from equipment_items where viking_id = ?";
        jdbcTemplate.update(sql, vikingId);
    }
}