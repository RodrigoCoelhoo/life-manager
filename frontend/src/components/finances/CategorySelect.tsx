import { useState } from "react";
import { ExpenseCategory, ExpenseCategoryType } from "../../services/finances/transaction/transaction.dto";

interface CategorySelectProps {
	value?: ExpenseCategory;
	allOption?: boolean;
	onChange: (value: ExpenseCategory) => void;
}

export default function CategorySelect({ value, allOption, onChange }: CategorySelectProps) {
	const [open, setOpen] = useState(false);

	const toggle = () => setOpen((o) => !o);
	const selectCurrency = (code: ExpenseCategory) => {
		onChange(code);
		setOpen(false);
	};

	return (
		<div className="relative inline-block w-full form-input m-0 p-0">
			{/* Button */}
			<button
				type="button"
				value={value}
				onClick={toggle}
				className="w-full flex items-center justify-between transition p-2 cursor-pointer"
			>
				<div className="flex gap-2 items-center justify-between">
					<span>
						{value ?? "ALL CATEGORIES"}
					</span>
					<span className="text-gray-500">
						{ExpenseCategoryType[value]}
					</span>
				</div>

				<svg
					className={`h-4 w-4 transition-transform ${open ? "" : "-rotate-90"}`}
					fill="none"
					stroke="currentColor"
					viewBox="0 0 24 24"
				>
					<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
				</svg>
			</button>

			{/* Dropdown */}
			{open && (
				<div className="absolute mt-1 w-full bg-foreground border border-primary rounded-lg shadow-lg max-h-48 overflow-y-auto z-20">

					{allOption === true && <div
						onClick={() => {
							onChange(undefined);
							setOpen(false);
						}}
						className="flex items-center px-3 py-2 cursor-pointer hover:bg-background text-sm opacity-70"
					>
						ALL CATEGORIES
					</div>}

					{Object.entries(ExpenseCategory).map(([key, value]) => (
						<div
							key={key}
							onClick={() => selectCurrency(value as ExpenseCategory)}
							className="flex items-center px-3 py-2 cursor-pointer hover:bg-background text-sm gap-2"
						>
							<span className="font-extralight">{value}</span>
							<span className="ml-auto opacity-70">
								{ExpenseCategoryType[value]}
							</span>
						</div>
					))}
				</div>
			)}

		</div>
	);
}