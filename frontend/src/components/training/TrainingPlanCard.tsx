import { useState } from "react";
import { Modal } from "../common/Modal";
import type { TrainingPlanResponseDTO } from "../../services/training/training-plan/training-plan.dto";
import TrainingPlanForm from "./TrainingPlanForm";

export default function TrainingPlanCard(trainingPlan: TrainingPlanResponseDTO) {
	const [isOpen, setIsOpen] = useState(false);

	return (
		<>
			<div
				className="p-4 bg-foreground h-100 rounded-lg flex flex-col justify-between gap-2 shadow-md cursor-pointer hover:shadow-xl transition hover:scale-[1.02] hover:border hover:border-secondary"
				onClick={() => setIsOpen(true)}
			>
				<div>
					<h2 className="text-xl font-bold">{trainingPlan.name}</h2>

					<div className="max-h-32 my-2 text-sm overflow-y-auto">
						<p className="mb-2">{trainingPlan.description || "No description available"}</p>
					</div>

					<ul className="max-h-60 overflow-y-auto flex flex-col list-disc list-inside font-extralight">
						{trainingPlan.exercises.map((exercise, index) => (
							<div className="flex justify-between">
								<li key={index} className="">
									{exercise.name}
								</li>
								<div className="text-xs text-gray-400">
									{exercise.type}
								</div>
							</div>
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
				/>
			</Modal>
		</>
	);
}
