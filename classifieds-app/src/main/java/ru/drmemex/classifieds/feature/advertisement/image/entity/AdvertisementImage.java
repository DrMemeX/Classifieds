package ru.drmemex.classifieds.feature.advertisement.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;

@Entity
@Table(name = "advertisement_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "advertisement_id",
            nullable = false
    )
    private Advertisement advertisement;

    @Column(
            name = "object_key",
            nullable = false
    )
    private String objectKey;

    @Column(
            name = "display_order",
            nullable = false
    )
    private Short displayOrder;
}
