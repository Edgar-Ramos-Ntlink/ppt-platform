package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.ResourceFile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceFileRepository extends JpaRepository<ResourceFile, Integer> {
  Optional<ResourceFile> findByTipoRecursoAndReferenciaAndTipoArchivo(
      String tipoRecurso, String referencia, String tipoArchivo);

  List<ResourceFile> findByTipoRecursoAndReferencia(String tipoRecurso, String referencia);
}
