package com.letscode.itau.bancoitau.model;

import com.letscode.itau.bancoitau.enumeration.TipoChavePix;
import lombok.*;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChavePix {
    @Id
    private Long id;
    private TipoChavePix tipoDeChave;
    private Requerente requerente;
}
