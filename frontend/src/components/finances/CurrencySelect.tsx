import { useState } from "react";
import { CurrencyCode, CurrencyData } from "../../services/finances/currencies.type";

interface CurrencySelectProps {
	value: CurrencyCode;
	onChange: (value: CurrencyCode) => void;
	disabled: boolean;
}

export default function CurrencySelect({ value, onChange, disabled }: CurrencySelectProps) {
	const [open, setOpen] = useState(false);

	const toggle = () => setOpen((o) => !o);
	const selectCurrency = (code: CurrencyCode) => {
		onChange(code);
		setOpen(false);
	};

	return (
		<div className="relative inline-block w-full form-input m-0 p-0">
			<button
				type="button"
				value={value}
				onClick={toggle}
				className={`w-full flex items-center justify-between transition p-2 ${disabled ? "cursor-not-allowed" : "cursor-pointer"}`}
				disabled={disabled}
			>
				<div className="flex gap-2 items-center">
					<span>
						{CurrencyData[value].name}
					</span>

					<span>
						({CurrencyData[value].symbol})
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

			{open && (
				<div className="absolute mt-1 w-full bg-foreground border border-secondary shadow-lg max-h-48 overflow-y-auto z-20">
					{Object.entries(CurrencyData).map(([code, currency]) => (
						<div
							key={code}
							onClick={() => selectCurrency(code as CurrencyCode)}
							className="flex items-center px-3 py-2 cursor-pointer hover:bg-background text-sm"
						>
							<span className="font-medium">{code}</span>
							<span className="ml-auto text-gray-500">{currency.symbol}</span>
						</div>
					))}
				</div>
			)}
		</div>
	);
}