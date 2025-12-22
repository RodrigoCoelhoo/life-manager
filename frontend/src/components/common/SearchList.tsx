import { useState, useEffect } from "react";
import { Pagination } from "./Pagination";
import type { PageResponseDTO } from "../../services/api.dto";

type SelectionMode = "single" | "multiple";

interface SearchItemProps {
	id: number;
	name: string;
	description?: string;
	type?: string;
}

interface SearchListProps<T extends SearchItemProps> {
	fetchItems: (
		page: number,
		size: number,
		query: string
	) => Promise<PageResponseDTO<T>>;

	selectionMode?: SelectionMode;
	selectedIds?: number[];
	selectedItems?: T[];
	onChange?: (items: T[]) => void;

	onClose?: () => void;
}

export const SearchList = <T extends SearchItemProps>({
	fetchItems,
	selectionMode = "single",
	selectedIds = [],
	selectedItems = [],
	onChange,
	onClose,
}: SearchListProps<T>) => {
	const [search, setSearch] = useState("");
	const [page, setPage] = useState(1);
	const [totalPages, setTotalPages] = useState(1);
	const [elementsPerPage] = useState(8);
	const [results, setResults] = useState<T[]>([]);
	const [debouncedSearch, setDebouncedSearch] = useState(search);

	const [selected, setSelected] = useState<Map<number, T>>(new Map());

	useEffect(() => {
		const map = new Map<number, T>();
		selectedItems.forEach(item => map.set(item.id, item));
		setSelected(map);
	}, [selectedItems]);

	useEffect(() => {
		const handler = setTimeout(() => {
			setDebouncedSearch(search);
		}, 500);

		return () => clearTimeout(handler);
	}, [search]);

	useEffect(() => {
		const loadResults = async () => {
			const data = await fetchItems(page - 1, elementsPerPage, debouncedSearch);
			setResults(data.content);
			setTotalPages(data.totalPages);
		};

		loadResults();
	}, [page, elementsPerPage, debouncedSearch, fetchItems]);

	const toggleItem = (item: T) => {
		setSelected(prev => {
			const next = new Map(prev);

			if (next.has(item.id)) {
				next.delete(item.id);
			} else {
				if (selectionMode === "single") {
					next.clear();
				}
				next.set(item.id, item);
			}

			onChange?.(Array.from(next.values()));
			return next;
		});
	};

	return (
		<div
			className="
				bg-foreground
				rounded-xl
				shadow-lg
				p-8
				max-h-[85vh]
				overflow-y-auto
				flex flex-col
				gap-4
				text-textcolor
				relative
			"
		>
			<button
				onClick={onClose}
				className="absolute top-2 right-4 text-white text-xl hover:text-gray-200 p-2 cursor-pointer"
			>
				✕
			</button>

			{selected.size > 0 && (
				<div className="bg-background/40 p-2 rounded-lg flex flex-wrap gap-2 w-90 sm:w-120">
					{Array.from(selected.values()).map(item => (
						<span
							key={item.id}
							onClick={() => toggleItem(item)}
							className="
								px-3 py-1 rounded-full text-sm
								bg-primary/30 text-textcolor
								cursor-pointer hover:bg-primary/50
								select-none whitespace-nowrap
							"
						>
							{item.name}
							<span className="ml-1">×</span>
						</span>
					))}
				</div>
			)}

			<input
				type="text"
				placeholder="Search..."
				className="form-input mt-6"
				value={search}
				onChange={(e) => {
					setSearch(e.target.value);
					setPage(1);
				}}
			/>

			{/* 🔹 Results list */}
			<div className="flex flex-col form-input w-90 sm:w-120 gap-1 p-0">
				{results.length > 0 ? (
					results.map((item, index) => {
						const isSelected = selected.has(item.id);
						const isDisabled = selectedIds.includes(item.id);

						return (
							<button
								key={item.id}
								type="button"
								disabled={isDisabled}
								onClick={(e) => {
									e.stopPropagation();
									if (!isDisabled) toggleItem(item);
								}}
								className={`
									p-2 flex justify-between items-center gap-2
									text-left w-full cursor-pointer
									${index % 2 === 0 ? "bg-gray-400/5" : "bg-background/50"}
									${isSelected
										? "bg-primary/30 ring-1 ring-primary"
										: "hover:bg-primary/20"}
									${isDisabled ? "opacity-40 cursor-not-allowed" : ""}
								`}
							>
								<span>{item.name}</span>

								<div className="text-xs text-gray-400">
									{isDisabled ? "Added" : item.type}
								</div>
							</button>
						);
					})
				) : (
					<p className="p-2 text-center text-gray-500 cursor-default">
						No items found.
					</p>
				)}
			</div>

			{/* 🔹 Pagination */}
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
