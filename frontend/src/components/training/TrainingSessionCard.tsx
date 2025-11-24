import { useState } from "react";
import type { TrainingSessionDTO, TrainingSessionResponseDTO } from "../../services/training/training-session/training-session.dto";
import { FaCircle } from "react-icons/fa";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import timezone from "dayjs/plugin/timezone";
import { Modal } from "../common/Modal";
import { TrainingSessionForm } from "./TrainingSessionForm";
dayjs.extend(utc);
dayjs.extend(timezone);

interface TrainingSessionCardProps extends TrainingSessionResponseDTO {
	onUpdate: (id: number, data: TrainingSessionDTO) => Promise<void>;
	onDelete: (id: number) => Promise<void>;
}

export default function TrainingSessionCard({ onUpdate, onDelete, ...trainingSession }: TrainingSessionCardProps) {
	const [isOpen, setIsOpen] = useState(false);

	const date = dayjs.utc(trainingSession.date).tz("Europe/Lisbon");
	const day = date.format("DD-MM-YYYY");
	const time = date.format("HH:mm");

	return (
		<>
			<div
				className="p-4 bg-foreground h-60 rounded-lg flex flex-col shadow-md cursor-pointer hover:shadow-xl transition hover:scale-[1.02] hover:border hover:border-secondary gap-3"
				onClick={() => setIsOpen(true)}
			>
				<div className="flex justify-between">
					<span className="font-bold">{day}</span>
					<span className="text-secondary font-extralight">{time}</span>
				</div>

				{trainingSession.exercises.length === 0 ? (
					<div>
						No exercises added yet
					</div>
				) : (

					<ul className="max-h-60 overflow-y-auto flex flex-col list-disc list-inside font-extralight">
						{trainingSession.exercises.map((exercise) => (
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
				)}
			</div >


			<Modal isOpen={isOpen} onClose={() => setIsOpen(false)}>
				<TrainingSessionForm
					trainingSessionId={trainingSession.id}
					onClose={() => setIsOpen(false)}
					onUpdate={onUpdate}
					onDelete={onDelete}
				/>
			</Modal>

		</>
	);
}