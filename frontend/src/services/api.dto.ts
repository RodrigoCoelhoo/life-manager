export interface PageResponseDTO<T> {
	content: T[];
	page: number;
	size: number;
	totalElements: string;
	totalPages: number;
}