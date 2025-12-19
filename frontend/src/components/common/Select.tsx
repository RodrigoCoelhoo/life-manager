import { useState } from "react";

interface BaseSelectProps<T> {
	items: T[];
	getLabel: (item: T) => string;
	disabled?: boolean;
}

interface SingleSelectProps<T> extends BaseSelectProps<T> {
	multiple?: false;
	value: T;
	onChange: (value: T) => void;
}

interface MultiSelectProps<T> extends BaseSelectProps<T> {
	multiple: true;
	value: T[];
	onChange: (value: T[]) => void;
	fixedLabel?: string;
}

type SelectProps<T> = SingleSelectProps<T> | MultiSelectProps<T>;

export function Select<T>(props: SelectProps<T>) {
	const { items, getLabel, disabled } = props;
	const [open, setOpen] = useState(false);

	const isMulti = props.multiple === true;

	const toggle = () => !disabled && setOpen(o => !o);

	const toggleItem = (item: T) => {
		if (!isMulti) {
			props.onChange(item);
			setOpen(false);
			return;
		}

		const selected = props.value.includes(item);
		props.onChange(
			selected
				? props.value.filter(v => v !== item)
				: [...props.value, item]
		);
	};

	const displayValue = () => {
		if (isMulti) {
			if (props.fixedLabel) return props.fixedLabel;
			return props.value.length
				? props.value.map(getLabel).join(", ")
				: "Select...";
		}
		return getLabel(props.value);
	};

	return (
		<div className="relative inline-block w-full form-input m-0 p-0">
			<button
				type="button"
				onClick={toggle}
				disabled={disabled}
				className={`w-full flex items-center gap-2 justify-between transition p-2
					${disabled ? "cursor-not-allowed opacity-60" : "cursor-pointer"}
				`}
			>
				<div className="flex gap-2 items-center truncate">
					<span className="truncate">{displayValue()}</span>
				</div>

				<svg
					className={`h-4 w-4 transition-transform ${open ? "" : "-rotate-90"}`}
					fill="none"
					stroke="currentColor"
					viewBox="0 0 24 24"
				>
					<path
						strokeLinecap="round"
						strokeLinejoin="round"
						strokeWidth={2}
						d="M19 9l-7 7-7-7"
					/>
				</svg>
			</button>

			{open && (
				<div className="absolute mt-1 w-full bg-foreground border border-secondary shadow-lg max-h-48 overflow-y-auto z-20">
					{items.map((item, i) => {
						const selected = isMulti && props.value.includes(item);

						return (
							<div
								key={i}
								onClick={() => toggleItem(item)}
								className={`flex items-center px-3 py-2 cursor-pointer text-sm
									${selected ? "bg-background font-medium" : "hover:bg-background"}
								`}
							>
								{isMulti && (
									<input
										type="checkbox"
										readOnly
										checked={selected}
										className="mr-2"
									/>
								)}

								<span className="truncate">{getLabel(item)}</span>
							</div>
						);
					})}
				</div>
			)}
		</div>
	);
}
