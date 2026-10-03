package cl.felruiz.apigeocl.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.felruiz.apigeocl.dto.ComunaDTO;
import cl.felruiz.apigeocl.dto.RegionDTO;
import cl.felruiz.apigeocl.exception.ResourceNotFoundException;
import cl.felruiz.apigeocl.mapper.ComunaMapper;
import cl.felruiz.apigeocl.mapper.RegionMapper;
import cl.felruiz.apigeocl.repository.ComunaRepository;
import cl.felruiz.apigeocl.repository.RegionRepository;
import lombok.RequiredArgsConstructor;

/**
 * Service con la lógica de negocio para Regiones y sus comunas.
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

  private final RegionRepository regionRepository;
  private final ComunaRepository comunaRepository;
  private final RegionMapper regionMapper;
  private final ComunaMapper comunaMapper;

  public List<RegionDTO> obtenerTodas() {
    return regionRepository.findAll(Sort.by("id"))
        .stream()
        .map(regionMapper::toDTO)
        .toList();
  }

  public List<ComunaDTO> obtenerComunasPorRegion(Long regionId) {
    if (!regionRepository.existsById(regionId)) {
      throw new ResourceNotFoundException("Región con id " + regionId + " no encontrada");
    }

    return comunaRepository.findByProvinciaRegionIdOrderByNombreAsc(regionId)
        .stream()
        .map(comunaMapper::toDTO)
        .toList();
  }
}
