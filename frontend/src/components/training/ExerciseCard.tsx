import { useState } from "react";
import type { ExerciseResponseDTO, ExerciseUpdateDTO } from "../../services/training/exercise/exercise.dto";
import { Modal } from "../common/Modal";
import ExerciseForm from "./ExerciseForm";

interface ExerciseCardProps extends ExerciseResponseDTO {
	onUpdate: (id: number, data: ExerciseUpdateDTO) => Promise<void>;
	onDelete: (id: number) => Promise<void>;
}

export default function ExerciseCard({ onUpdate, onDelete, ...exercise }: ExerciseCardProps) {
	const [isOpen, setIsOpen] = useState(false);

	return (
		<>
			<div
				className="p-4 bg-foreground rounded-lg flex flex-col justify-between shadow-md cursor-pointer hover:shadow-xl transition hover:scale-[1.02] hover:border hover:border-secondary"
				onClick={() => setIsOpen(true)}
			>
				<h2 className="text-xl font-bold">{exercise.name}</h2>

				<div className="flex-1 my-2 text-sm overflow-y-auto">
					<p className="mb-2 font-extralight">{exercise.description || "No description available"}</p>
				</div>

				<a
					href={exercise.demoUrl || "#"}
					target="_blank"
					rel="noreferrer"
					className={`text-secondary ${exercise.demoUrl === "" && "pointer-events-none"} hover:underline hover:text-primary w-fit`}
				>
					{exercise.demoUrl !== "" ? "Watch Demo" : "No Demo"}
				</a>

				<div className="flex justify-between">
					<span className="text-xs text-gray-400">
						Updated: {new Date(exercise.updatedAt).toLocaleDateString()}
					</span>
					<span className="text-xs text-gray-400">{exercise.type}</span>
				</div>
			</div>

			<Modal isOpen={isOpen} onClose={() => setIsOpen(false)}>
				<ExerciseForm
					exercise={exercise}
					onClose={() => setIsOpen(false)}
					onUpdate={onUpdate}
					onDelete={onDelete}
				/>
			</Modal>
		</>
	);
}
