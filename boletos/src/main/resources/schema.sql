create table boleto(id IDENTITY not null primary key, valor numeric(10, 2) not null,	vencimento date not null ,	uuid_associado uuid not null,documento_pagador varchar(14) not null,nome_pagador varchar(50) not null,situacao varchar not null,nome_fantasia_pagador varchar(50));
create unique index uq_boleto_id_uuid_associado on boleto(id, uuid_associado);
