import { useState } from "react";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import timezone from "dayjs/plugin/timezone";
import { Modal } from "../common/Modal";
import type { MealDetailsDTO, MealDTO } from "../../services/nutrition/meal/meal.dto";
import { MealForm } from "./MealForm";
dayjs.extend(utc);
dayjs.extend(timezone);

interface MealCardProps extends MealDetailsDTO {
	onUpdate: (id: number, data: MealDTO) => Promise<void>;
	onDelete: (id: number) => Promise<void>;
}

export default function MealCard({ onUpdate, onDelete, ...meal }: MealCardProps) {
	const [isOpen, setIsOpen] = useState(false);

	const date = dayjs.utc(meal.meal.dateTime);
	const day = date.format("DD/MM/YYYY");
	const time = date.format("HH:mm");

	return (
		<>
			<div
				className="p-4 bg-foreground rounded-lg flex flex-col shadow-md cursor-pointer hover:shadow-xl transition hover:scale-[1.02] hover:border hover:border-secondary gap-3"
				onClick={() => setIsOpen(true)}
			>
				<div className="flex justify-between">
					<span className="font-bold">{day}</span>
					<span className="text-secondary font-extralight">{time}</span>
				</div>

				{meal.nutritionalLabel.length === 0 ? (
					<div>
						No nutritional label added yet
					</div>
				) : (

					<div className="max-h-60 overflow-y-auto flex flex-col font-extralight">
						{meal.nutritionalLabel.map((nutrient, index) => {
							const rowStyle = index % 2 === 0 ? "bg-foreground/90 p-1" : "bg-foreground/30 p-1";

							return (
								<div
									key={nutrient.nutrient}
									className={`
										flex 
										justify-between 
										p-1
										${rowStyle}
										${nutrient.nutrient === "CALORIES" ? "border-b border-primary/50" : ""}
									`}
								>
									<div className="flex flex-row gap-2 items-center">
										<span className={`
											font-semibold 
											${nutrient.nutrient === "CALORIES" ? "text-secondary" : "text-textcolor/80"}
											text-sm
											`}
										>
											{nutrient.nutrient}
										</span>
									</div>
									<div className="text-sm text-gray-400">{nutrient.amount.toFixed(2)} {nutrient.unit}</div>
								</div>
							)
						}
						)}
					</div>
				)}
			</div >


			<Modal isOpen={isOpen} onClose={() => setIsOpen(false)}>
				<MealForm
					meal={meal.meal}
					onClose={() => setIsOpen(false)}
					onUpdate={onUpdate}
					onDelete={onDelete}
				/>
			</Modal>

		</>
	);
}