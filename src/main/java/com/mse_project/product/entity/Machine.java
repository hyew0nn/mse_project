package com.mse_project.product.entity;

import com.mse_project.common.BaseTimeEntity;
import com.mse_project.product.entity.enums.MachineStatus;
import com.mse_project.product.entity.enums.OperationMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Machines")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Machine extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machine_id")
    private Long machineId;

    @Column(name = "machine_name", nullable = false)
    private String machineName;

    @Column(name = "serial_number", unique = true, nullable = false)
    private String serialNumber;

    @Column(name = "uph")
    private Integer uph;

    @Enumerated(EnumType.STRING)
    @Column(name = "op_mode", length = 20)
    private OperationMode opMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private MachineStatus status = MachineStatus.OFF;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "updated_by")
    private String updatedBy;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Part> parts = new ArrayList<>();
}