import { useState } from "react";
import { Modal } from "../common/Modal";
import type { TrainingPlanResponseDTO, TrainingPlanUpdateDTO } from "../../services/training/training-plan/training-plan.dto";
import TrainingPlanForm from "./TrainingPlanForm";
import { FaCircle } from "react-icons/fa";

interface TrainingPlanCardProps extends TrainingPlanResponseDTO {
	onUpdate: (id: number, data: TrainingPlanUpdateDTO) => Promise<void>;
	onDelete: (id: number) => Promise<void>;
}

export default function TrainingPlanCard({ onUpdate, onDelete, ...trainingPlan }: TrainingPlanCardProps) {
	const [isOpen, setIsOpen] = useState(false);

	return (
		<>
			<div
				className="p-4 bg-foreground rounded-lg flex flex-col justify-between gap-2 shadow-md cursor-pointer hover:shadow-xl transition hover:scale-[1.02] hover:border hover:border-secondary"
				onClick={() => setIsOpen(true)}
			>
				<div>
					<h2 className="text-xl font-bold">{trainingPlan.name}</h2>

					<div className="max-h-32 my-2 text-sm overflow-y-auto font-extralight">
						<p className="mb-2">{trainingPlan.description || "No description available"}</p>
					</div>

					<ul className="max-h-60 overflow-y-auto flex flex-col list-disc list-inside font-extralight">
						{trainingPlan.exercises.map((exercise) => (
							<li
								key={exercise.id}
								className="flex justify-between"
							>
								<div className="flex flex-row gap-2 items-center">
									<FaCircle size={6} className="text-secondary mt-1" />
									<span className="">{exercise.name}</span>
								</div>
								<div className="text-xs text-gray-400">{exercise.type}</div>
							</li>
						))}
					</ul>
				</div>

				<div className="flex justify-between">
					<span className="text-xs text-gray-400">
						Updated: {new Date(trainingPlan.updatedAt).toLocaleDateString()}
					</span>
				</div>
			</div>


			<Modal isOpen={isOpen} onClose={() => setIsOpen(false)}>
				<TrainingPlanForm
					trainingPlan={trainingPlan}
					onClose={() => setIsOpen(false)}
					onUpdate={onUpdate}
					onDelete={onDelete}
				/>
			</Modal>
		</>
	);
}
