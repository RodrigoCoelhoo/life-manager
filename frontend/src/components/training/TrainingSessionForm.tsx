import { useRef, useState } from "react";
import ToggleButton from "../common/ToggleButton";
import { FaSearch } from "react-icons/fa";
import type { TrainingPlanResponseDTO } from "../../services/training/training-plan/training-plan.dto";
import { Modal } from "../common/Modal";
import { SearchList } from "../common/SearchList";
import { trainingPlanService } from "../../services/training/training-plan/training-plan.service";
import { ButtonField } from "../common/ButtonField";
import { DateTimeField } from "../common/DateTimeField";
import { FaEye } from "react-icons/fa";
import { FaPencil } from "react-icons/fa6";

interface TrainingSessionFormProps {
	trainingSessionId?: number;
	onClose: () => void;
	/**onCreate?: (trainingPlan: TrainingPlanDTO) => Promise<void>;
	onUpdate?: (id: number, data: TrainingPlanUpdateDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
	 */
}

export const TrainingSessionForm = ({ onClose }: TrainingSessionFormProps) => {
	const [isEditing, setIsEditing] = useState<boolean>(false);
	const [trainingPlanOptions, setTrainingPlanOptions] = useState<boolean>(false);

	const [trainingPlan, setTrainingPlan] = useState<TrainingPlanResponseDTO>();
	const [date, setDate] = useState<string>("");
	const [time, setTime] = useState<string>("");

	const trainingPlanRef = useRef<any>(null);
	const dateRef = useRef<any>(null);
	const timeRef = useRef<any>(null);

	const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const isTrainingPlanValid = trainingPlanRef.current?.validate();
		const isDateValid = dateRef.current?.validate();
		const isTimeValid = timeRef.current?.validate();

		if (!isTrainingPlanValid || !isDateValid || !isTimeValid) {
			return;
		}
	};

	return (
		<>
			<div className="
			bg-foreground 
			rounded-xl 
			shadow-lg 
			p-12
			max-h-[85vh] 
			overflow-y-auto 
			flex flex-col
			gap-4
			text-textcolor
			"
			>
				<div className="flex flex-row absolute top-2 right-4 gap-3 items-center">
					{isEditing && <span className="text-sm text-gray-400">You are in edit mode</span>}
					{isEditing ? <FaPencil /> : <FaEye />}
					<ToggleButton
						onToggle={setIsEditing}
					/>
					<button
						onClick={onClose}
						className=" text-white text-xl hover:text-gray-200 p-2 cursor-pointer"
					>
						✕
					</button>
				</div>

				<form className="flex flex-col space-y-3 w-80 sm:w-100" onSubmit={handleSubmit}>
					<label htmlFor="trainingPlanName" className="text-sm mb-1">
						Training Plan
					</label>
					<ButtonField
						ref={trainingPlanRef}
						value={trainingPlan?.name || ""}
						placeholder="Select a training plan"
						onClick={() => setTrainingPlanOptions(true)}
						rules={[val => (!!val ? true : "Training plan is required")]}
						icon={<FaSearch size={16} className="text-secondary" />}
						disabled={!isEditing}
					/>

					<div className="mt-2 flex flex-row gap-6">
						<div className="flex flex-col">
							<label htmlFor="date" className="text-sm mb-1">
								Date
							</label>
							<DateTimeField
								ref={dateRef}
								type="date"
								value={date}
								onChange={setDate}
								rules={[val => (!!val ? true : "Date is required")]}
								disabled={!isEditing}
							/>
						</div>
						<div className="flex flex-col">
							<label htmlFor="time" className="text-sm mb-1">
								Time
							</label>
							<DateTimeField
								ref={timeRef}
								type="time"
								value={time}
								onChange={setTime}
								rules={[val => (!!val ? true : "Time is required")]}
								disabled={!isEditing}
							/>
						</div>
					</div>

					<button
						type="submit"
						className="form-submit"
					>
						wad
					</button>
				</form>
			</div>

			<Modal isOpen={trainingPlanOptions} onClose={() => setTrainingPlanOptions(false)}>
				<SearchList<TrainingPlanResponseDTO>
					fetchItems={trainingPlanService.getTrainingPlans}
					onSelect={(trainingPlan) => {
						setTrainingPlan(trainingPlan);
						setTrainingPlanOptions(false);
					}}
					onClose={() => setTrainingPlanOptions(false)}
				/>
			</Modal>
		</>
	);
};