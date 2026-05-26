package ru.mephi.vikingdemo.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.EquipmentItemEntity;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.model.VikingEntity;


@Repository
public class VikingStorage {

    private final VikingRepository vikingRepository;
    private final EquipmentItemRepository equipmentItemRepository;
    private final VikingMapper vikingMapper;

    public VikingStorage(
            VikingRepository vikingRepository,
            EquipmentItemRepository equipmentItemRepository,
            VikingMapper vikingMapper
    ) {
        this.vikingRepository = vikingRepository;
        this.equipmentItemRepository = equipmentItemRepository;
        this.vikingMapper = vikingMapper;
    }

    @Transactional
    public Viking save(Viking viking) {
        Integer vikingId = vikingRepository.save(
                vikingMapper.toVikingEntity(viking)
        );

        for (EquipmentItem item : viking.equipment()) {
            equipmentItemRepository.save(
                    vikingMapper.toEquipmentItemEntity(vikingId, item)
            );
        }

        return viking;
    }

    public List<Viking> findAll() {
        List<VikingEntity> vikingEntities = vikingRepository.findAll();
        List<EquipmentItemEntity> equipmentEntities = equipmentItemRepository.findAll();

        Map<Integer, List<EquipmentItemEntity>> equipmentByVikingId = equipmentEntities.stream()
                .collect(Collectors.groupingBy(EquipmentItemEntity::vikingId));

        return vikingEntities.stream()
                .map(vikingEntity -> vikingMapper.toViking(
                        vikingEntity,
                        equipmentByVikingId.getOrDefault(vikingEntity.id(), List.of())
                ))
                .toList();
    }

    @Transactional
    public void deleteById(int id) {
        vikingRepository.deleteById(id);
    }

    @Transactional
    public void update(int id, Viking viking) {
        vikingRepository.update(id, vikingMapper.toVikingEntity(viking));

        equipmentItemRepository.deleteByVikingId(id);
        for (EquipmentItem item : viking.equipment()) {
            equipmentItemRepository.save(
                    vikingMapper.toEquipmentItemEntity(id, item)
            );
        }
    }
}
