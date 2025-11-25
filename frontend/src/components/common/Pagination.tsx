interface PaginationProps {
	currentPage: number;
	totalPages: number;
	onPageChange: (page: number) => void;
}

export function Pagination({ currentPage, totalPages, onPageChange }: PaginationProps) {
	if (totalPages <= 1) return null;

	const generatePages = () => {
		const pages: (number | string)[] = [];

		const showLeftJump = currentPage > 4;
		const showRightJump = currentPage < totalPages - 3;

		pages.push(1);
		if (showLeftJump) pages.push("...");

		const start = Math.max(2, currentPage - 2);
		const end = Math.min(totalPages - 1, currentPage + 2);

		for (let i = start; i <= end; i++) {
			pages.push(i);
		}

		if (showRightJump) pages.push("...");

		if (totalPages > 1) pages.push(totalPages);

		return pages;
	};

	const pages = generatePages();

	return (
		<div className="flex gap-2 items-center justify-center mt-6 flex-wrap">
			{pages.map((p, i) =>
				p === "..." ? (
					<span key={i} className="px-2 text-textcolor select-none">
						…
					</span>
				) : (
					<button
						key={i}
						onClick={() => onPageChange(p as number)}
						className={`px-3 py-1 rounded-lg drop-shadow-[0px_4px_6px_rgba(0,0,0,0.4)]
              				${p === currentPage ? 
								"bg-primary text-primary-foreground": 
								"bg-foreground hover:bg-muted"
							}
            			`}
					>
						{p}
					</button>
				)
			)}
		</div>
	);
}
