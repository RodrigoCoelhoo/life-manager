import { useEffect, useState, useRef } from "react";
import { SearchBar } from "../common/SearchBar";
import type { PageResponseDTO } from "../../services/api.dto";
import type { ExerciseResponseDTO } from "../../services/training/exercise/exercise.dto";

interface ExercisesSearchProps {
	getExercises: (
		page: number,
		size: number,
		name?: string
	) => Promise<PageResponseDTO<ExerciseResponseDTO>>;
	setExercise: (id: number) => void;
}

export const ExercisesSearch = ({ getExercises, setExercise }: ExercisesSearchProps) => {
	const [search, setSearch] = useState("");
	const [exercises, setExercises] = useState<ExerciseResponseDTO[]>([]);
	const [loading, setLoading] = useState(false);
	const [showDropdown, setShowDropdown] = useState(false);
	const dropdownRef = useRef<HTMLDivElement>(null);
	const searchBarRef = useRef<HTMLInputElement>(null);

	useEffect(() => {
		const fetchExercises = async () => {
			setLoading(true);
			try {
				const res = await getExercises(0, 10, search || undefined);

				const filteredExercise = res.content.filter(
					exercise => exercise.type === 'SET_REP'
				);
				setExercises(filteredExercise);
			} finally {
				setLoading(false);
			}
		};

		fetchExercises();
	}, [search, getExercises]);

	useEffect(() => {
		const handleClickOutside = (event: MouseEvent) => {
			if (
				dropdownRef.current &&
				!dropdownRef.current.contains(event.target as Node) &&
				searchBarRef.current &&
				!searchBarRef.current.contains(event.target as Node)
			) {
				setShowDropdown(false);
			}
		};

		document.addEventListener("mousedown", handleClickOutside);
		return () => {
			document.removeEventListener("mousedown", handleClickOutside);
		};
	}, []);

	const handleExerciseSelect = (exercise: ExerciseResponseDTO) => {
		setSearch("");
		setExercise(exercise.id);
		setShowDropdown(false);
	};

	const handleSearchBarFocus = () => {
		setShowDropdown(true);
	};

	return (
		<div className="relative w-full" ref={dropdownRef}>
			<SearchBar
				value={search}
				onChange={setSearch}
				onFocus={handleSearchBarFocus}
				placeholder="Search exercises..."
				ref={searchBarRef}
			/>
			{showDropdown && (
				<div className="absolute z-10 mt-1 w-full rounded border border-primary bg-background shadow">
					{loading && (
						<div className="p-2 text-sm text-gray-500">Loading...</div>
					)}
					{!loading && exercises.length === 0 && (
						<div className="p-2 text-sm text-gray-500">No results</div>
					)}
					<ul>
						{exercises.map((exercise) => (
							<li
								key={exercise.id}
								className="cursor-pointer p-2 hover:bg-foreground"
								onClick={() => handleExerciseSelect(exercise)}
							>
								{exercise.name}
							</li>
						))}
					</ul>
				</div>
			)}
		</div>
	);
};