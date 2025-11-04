package org.denisova.integrationapp.service;

import jakarta.transaction.Transactional;
import org.denisova.integrationapp.client.dto.CmsSpareDto;
import org.denisova.integrationapp.domain.SparePart;
import org.denisova.integrationapp.domain.SparePartVersion;
import org.denisova.integrationapp.repo.SparePartRepository;
import org.denisova.integrationapp.repo.SparePartVersionRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Service
public class UpsertService {
    private final SparePartRepository spareRepo;
    private final SparePartVersionRepository versionRepo;

    public UpsertService(SparePartRepository spareRepo, SparePartVersionRepository versionRepo) {
        this.spareRepo = spareRepo;
        this.versionRepo = versionRepo;
    }

    @Transactional
    public boolean upsertFromCms(CmsSpareDto dto, OffsetDateTime syncStartedAt, Counters counters) {
        var existing = spareRepo.findBySpareCode(dto.getSpareCode()).orElse(null);
        if (existing == null) {
            var entity = mapNew(dto);
            entity.setLastSeenAt(syncStartedAt);
            spareRepo.save(entity);
            versionRepo.save(mapVersion(dto, "CREATED"));
            counters.inserted++;
            return true;
        } else {
            boolean changed = applyChanges(existing, dto);
            existing.setLastSeenAt(syncStartedAt);
            if (changed) {
                spareRepo.save(existing);
                versionRepo.save(mapVersion(dto, "UPDATED"));
                counters.updated++;
            }
            return changed;
        }
    }

    private SparePart mapNew(CmsSpareDto d) {
        var e = new SparePart();
        e.setSpareCode(d.getSpareCode());
        e.setName(d.getSpareName());
        e.setDescription(d.getSpareDescription());
        e.setType(d.getSpareType());
        e.setStatus(d.getSpareStatus());
        e.setPrice(d.getPrice());
        e.setQuantity(d.getQuantity());
        e.setUpdatedAt(d.getUpdatedAt() == null ? null : d.getUpdatedAt().atOffset(ZoneOffset.UTC));
        e.setIsActive(true);
        return e;
    }

    private boolean applyChanges(SparePart e, CmsSpareDto d) {
        boolean changed = false;
        changed |= setIfDiff(() -> e.getName(),       v -> e.setName(v), d.getSpareName());
        changed |= setIfDiff(() -> e.getDescription(),v -> e.setDescription(v), d.getSpareDescription());
        changed |= setIfDiff(() -> e.getType(),       v -> e.setType(v), d.getSpareType());
        changed |= setIfDiff(() -> e.getStatus(),     v -> e.setStatus(v), d.getSpareStatus());
        changed |= setIfDiff(() -> e.getPrice(),      v -> e.setPrice(v), d.getPrice());
        changed |= setIfDiff(() -> e.getQuantity(),   v -> e.setQuantity(v), d.getQuantity());
        changed |= setIfDiff(() -> e.getUpdatedAt(),
                v -> e.setUpdatedAt(v),
                d.getUpdatedAt() == null ? null : d.getUpdatedAt().atOffset(ZoneOffset.UTC));
        if (Boolean.FALSE.equals(e.getIsActive())) { e.setIsActive(true); changed = true; }
        return changed;
    }

    private <T> boolean setIfDiff(java.util.function.Supplier<T> getter,
                                  java.util.function.Consumer<T> setter, T newVal) {
        T oldVal = getter.get();
        if (!Objects.equals(oldVal, newVal)) { setter.accept(newVal); return true; }
        return false;
    }

    private SparePartVersion mapVersion(CmsSpareDto d, String kind) {
        var v = new SparePartVersion();
        v.setSpareCode(d.getSpareCode());
        v.setName(d.getSpareName());
        v.setDescription(d.getSpareDescription());
        v.setType(d.getSpareType());
        v.setStatus(d.getSpareStatus());
        v.setPrice(d.getPrice());
        v.setQuantity(d.getQuantity());
        v.setUpdatedAt(d.getUpdatedAt() == null ? null : d.getUpdatedAt().atOffset(ZoneOffset.UTC));
        v.setChangeKind(kind);
        return v;
    }

    @Transactional
    public int deactivateMissing(OffsetDateTime seenAfter) {
        var all = spareRepo.findAll();
        int deactivated = 0;
        for (var e : all) {
            if (e.getLastSeenAt() == null || e.getLastSeenAt().isBefore(seenAfter)) {
                if (Boolean.TRUE.equals(e.getIsActive())) {
                    e.setIsActive(false);
                    spareRepo.save(e);
                    var v = new SparePartVersion();
                    v.setSpareCode(e.getSpareCode());
                    v.setName(e.getName());
                    v.setDescription(e.getDescription());
                    v.setType(e.getType());
                    v.setStatus(e.getStatus());
                    v.setPrice(e.getPrice());
                    v.setQuantity(e.getQuantity());
                    v.setUpdatedAt(e.getUpdatedAt());
                    v.setChangeKind("DEACTIVATED");
                    versionRepo.save(v);
                    deactivated++;
                }
            }
        }
        return deactivated;
    }

    public static class Counters {
        public int inserted = 0;
        public int updated = 0;
    }
}
