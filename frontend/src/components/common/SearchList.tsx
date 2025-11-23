import { useState, useEffect } from "react";
import { Pagination } from "./Pagination";
import type { PageResponseDTO } from "../../services/api.dto";

interface SearchListProps<T extends SearchItemProps> {
	fetchItems: (page: number, size: number, query: string) => Promise<PageResponseDTO<T>>;
	onSelect?: (item: T) => void;
	onClose?: () => void;
}

interface SearchItemProps {
	id: number;
	name: string;
	description?: string;
	type?: string;
}

export const SearchList = <T extends SearchItemProps>({ onSelect, fetchItems, onClose }: SearchListProps<T>) => {
	const [search, setSearch] = useState<string>("");
	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [elementsPerPage] = useState<number>(8);
	const [results, setResults] = useState<T[]>([]);
	const [debouncedSearch, setDebouncedSearch] = useState(search);

	useEffect(() => {
		const handler = setTimeout(() => {
			setDebouncedSearch(search);
		}, 500);

		return () => {
			clearTimeout(handler);
		};
	}, [search]);

	useEffect(() => {
		const loadResults = async () => {
			const data = await fetchItems(page - 1, elementsPerPage, debouncedSearch);
			setResults(data.content);
			setTotalPages(data.totalPages);
		};
		loadResults();
	}, [page, elementsPerPage, debouncedSearch, fetchItems]);

	return (
		<div className="
      bg-foreground 
      rounded-xl 
      shadow-lg 
      p-12
      max-h-[85vh] 
      overflow-y-auto 
      flex flex-col
      gap-4
      text-textcolor"
		>
			<button
				onClick={onClose}
				className="absolute top-2 right-4 text-white text-xl hover:text-gray-200 p-2 cursor-pointer"
			>
				✕
			</button>
			<input
				type="text"
				placeholder="Search..."
				className="form-input"
				value={search}
				onChange={(event) => {
					setSearch(event.target.value);
					setPage(1);
				}}
			/>

			<div className="flex flex-col form-input w-90 sm:w-120 gap-1 p-0">
				{results.length > 0 ? (
					results.map((item, index) => (
						<button
							type="button"
							key={item.id}
							className={`${index % 2 === 0 ? 'bg-gray-400/5' : 'bg-background/50'} p-2 flex justify-between items-center gap-2 hover:bg-primary/30 rounded-lg text-left w-full cursor-pointer`}
							onClick={(e) => {
								e.stopPropagation();
								onSelect?.(item);
							}}
						>
							<span>{index + 1}. {item.name}</span>
							<div className="text-xs text-gray-400">{item.type}</div>
						</button>
					))
				) : (
					<p className="p-2 text-center text-gray-500 cursor-default">No items found.</p>
				)}
			</div>

			<div className="mb-4">
				<Pagination
					currentPage={page}
					totalPages={totalPages}
					onPageChange={(p: number) => setPage(p)}
				/>
			</div>
		</div>
	);
};
