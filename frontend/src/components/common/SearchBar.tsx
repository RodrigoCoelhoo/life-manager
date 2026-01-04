import { useState, useEffect, forwardRef } from "react";

interface SearchBarProps {
	value: string;
	onChange: (debouncedValue: string) => void;
	delay?: number;
	placeholder?: string;
	className?: string;
	onFocus?: () => void;
}

export const SearchBar = forwardRef<HTMLInputElement, SearchBarProps>(({
	value,
	onChange,
	delay = 500,
	placeholder = "Search...",
	className = "form-input w-full",
	onFocus,
}: SearchBarProps, ref) => {
	const [input, setInput] = useState(value);

	useEffect(() => {
		const handler = setTimeout(() => {
			onChange(input);
		}, delay);

		return () => clearTimeout(handler);
	}, [input, delay, onChange]);

	useEffect(() => {
		setInput(value);
	}, [value]);

	return (
		<input
			ref={ref}
			type="text"
			value={input}
			placeholder={placeholder}
			className={className}
			onChange={(e) => setInput(e.target.value)}
			onFocus={onFocus}
		/>
	);
});