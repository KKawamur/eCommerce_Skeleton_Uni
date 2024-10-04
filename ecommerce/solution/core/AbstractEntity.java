package thkoeln.archilab.ecommerce.solution.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@EqualsAndHashCode
@MappedSuperclass
public abstract class AbstractEntity {

  @Id
  @Getter
  protected UUID id;

  protected AbstractEntity() {
    this.id = UUID.randomUUID();
  }
}
