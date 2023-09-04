create table boleto(id IDENTITY not null primary key, valor numeric(10, 2) not null,	vencimento date not null ,	uuid_associado uuid not null,documento_pagador varchar(14) not null,nome_pagador varchar(50) not null,situacao varchar not null,nome_fantasia_pagador varchar(50));
create unique index uq_boleto_id_uuid_associado on boleto(id, uuid_associado);
insert into boleto (id, valor, vencimento, uuid_associado, documento_pagador, nome_pagador, situacao) values (1, 10, DATEADD(day, 1, CURRENT_TIMESTAMP()), '2D1EBC5B7D2741979CF0E84451C5BBB1', '49351840000', 'usuário teste', 'PAGO')
