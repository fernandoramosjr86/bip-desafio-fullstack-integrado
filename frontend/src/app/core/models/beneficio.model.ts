export interface Beneficio {
  id: number;
  nome: string;
  descricao: string | null;
  valor: number;
  ativo: boolean;
  version: number;
}

export interface BeneficioRequest {
  nome: string;
  descricao: string | null;
  valor: number;
  ativo: boolean;
}

export interface TransferenciaRequest {
  beneficioOrigemId: number;
  beneficioDestinoId: number;
  valor: number;
}

export interface PageResponse<T> {
  items: T[];
  totalItems: number;
  page: number;
  size: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}
