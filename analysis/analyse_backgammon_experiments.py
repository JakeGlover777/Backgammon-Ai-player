from pathlib import Path
import csv
import math
import statistics

import matplotlib.pyplot as plt
from scipy.stats import binomtest, linregress, norm, pearsonr


PROJECT_ROOT = Path(__file__).resolve().parent.parent
ROOT = PROJECT_ROOT / "experiment-output" / "final"
OUTPUT = PROJECT_ROOT / "analysis-output" / "final"

OUTPUT.mkdir(exist_ok=True)


EXPERIMENTS = {
    "Random vs Heuristic":
        "random_ai-vs-heuristic_ai-depth-2-budget-5000-2026-08-30_23-48-06",

    "Depth 1 / 10k":
        "expectimax_ai-vs-heuristic_ai-depth-1-budget-10000-2026-08-30_23-51-11",

    "Depth 2 / 1k":
        "expectimax_ai-vs-heuristic_ai-depth-2-budget-1000-2026-08-30_23-53-19",

    "Depth 2 / 5k":
        "expectimax_ai-vs-heuristic_ai-depth-2-budget-5000-2026-08-31_00-15-09",

    "Depth 2 / 10k":
        "expectimax_ai-vs-heuristic_ai-depth-2-budget-10000-2026-08-31_01-42-29",

    "Depth 1 / untruncated":
        "expectimax_ai-vs-heuristic_ai-depth-1-budget-1000000000-2026-08-31_02-03-00",
}


def read_csv(path):
    with path.open(newline="", encoding="utf-8-sig") as file:
        return list(csv.DictReader(file))


def wilson_interval(wins, games, z=1.959963984540054):
    win_rate = wins / games

    denominator = 1 + (z * z / games)

    centre = (
        win_rate
        + (z * z / (2 * games))
    ) / denominator

    half_width = (
        z
        * math.sqrt(
            (win_rate * (1 - win_rate) / games)
            + (z * z / (4 * games * games))
        )
        / denominator
    )

    return centre - half_width, centre + half_width


def percentile(values, percentile_value):
    values = sorted(values)

    position = (len(values) - 1) * percentile_value

    lower = math.floor(position)
    upper = math.ceil(position)

    if lower == upper:
        return values[lower]

    return (
        values[lower] * (upper - position)
        + values[upper] * (position - lower)
    )


def get_winner_ai(game):
    if game["winner"] == "WHITE":
        return game["white_ai"]

    if game["winner"] == "BLACK":
        return game["black_ai"]

    return None


def analyse_experiment(name, folder_name):
    folder = ROOT / folder_name

    games = read_csv(folder / "games.csv")
    decisions = read_csv(folder / "decisions.csv")

    if name == "Random vs Heuristic":
        target_ai = "HEURISTIC_AI"
    else:
        target_ai = "EXPECTIMAX_AI"

    completed_games = [
        game
        for game in games
        if game["winner"] != "NONE"
    ]

    wins = sum(
        get_winner_ai(game) == target_ai
        for game in completed_games
    )

    game_count = len(completed_games)
    win_rate = wins / game_count

    confidence_low, confidence_high = wilson_interval(
        wins,
        game_count
    )

    p_value = binomtest(
        wins,
        game_count,
        0.5
    ).pvalue

    target_decisions = [
        decision
        for decision in decisions
        if decision["ai_type"] == target_ai
    ]

    decision_times = [
        int(decision["decision_time_ns"]) / 1_000_000
        for decision in target_decisions
        if decision["decision_time_ns"]
    ]

    node_counts = [
        int(decision["nodes_evaluated"])
        for decision in target_decisions
        if decision["nodes_evaluated"]
    ]

    node_time_pairs = [
        (
            int(decision["nodes_evaluated"]),
            int(decision["decision_time_ns"]) / 1_000_000
        )
        for decision in target_decisions
        if decision["nodes_evaluated"]
        and decision["decision_time_ns"]
        and int(decision["nodes_evaluated"]) > 0
        and int(decision["decision_time_ns"]) > 0
    ]

    budget_hits = [
        decision["budget_reached"].lower() == "true"
        for decision in target_decisions
        if decision["budget_reached"]
    ]

    colour_results = {}

    for colour in ("WHITE", "BLACK"):
        colour_games = [
            game
            for game in completed_games
            if (
                game["white_ai"]
                if colour == "WHITE"
                else game["black_ai"]
            ) == target_ai
        ]

        colour_wins = sum(
            game["winner"] == colour
            for game in colour_games
        )

        colour_results[colour] = {
            "wins": colour_wins,
            "games": len(colour_games),
            "win_rate": colour_wins / len(colour_games),
        }

    return {
        "name": name,
        "target_ai": target_ai,
        "games": game_count,
        "wins": wins,
        "win_rate": win_rate,
        "ci_low": confidence_low,
        "ci_high": confidence_high,
        "p_value": p_value,
        "incomplete": len(games) - game_count,

        "average_turns": statistics.mean(
            int(game["turn_count"])
            for game in games
        ),

        "mean_time_ms": statistics.mean(
            decision_times
        ),

        "median_time_ms": statistics.median(
            decision_times
        ),

        "sd_time_ms": statistics.stdev(
            decision_times
        ),

        "p95_time_ms": percentile(
            decision_times,
            0.95
        ),

        "mean_nodes": (
            statistics.mean(node_counts)
            if node_counts
            else None
        ),

        "median_nodes": (
            statistics.median(node_counts)
            if node_counts
            else None
        ),

        "sd_nodes": (
            statistics.stdev(node_counts)
            if len(node_counts) > 1
            else None
        ),

        "p95_nodes": (
            percentile(node_counts, 0.95)
            if node_counts
            else None
        ),

        "budget_hit_rate": (
            sum(budget_hits) / len(budget_hits)
            if budget_hits
            else None
        ),

        "node_time_pairs": node_time_pairs,

        "white": colour_results["WHITE"],
        "black": colour_results["BLACK"],
    }


def compare_proportions(first, second):
    pooled_rate = (
        first["wins"] + second["wins"]
    ) / (
        first["games"] + second["games"]
    )

    standard_error = math.sqrt(
        pooled_rate
        * (1 - pooled_rate)
        * (
            (1 / first["games"])
            + (1 / second["games"])
        )
    )

    z_score = (
        first["win_rate"]
        - second["win_rate"]
    ) / standard_error

    p_value = 2 * (
        1 - norm.cdf(abs(z_score))
    )

    return z_score, p_value


def prepare_axis(axis):
    axis.spines["top"].set_visible(False)
    axis.spines["right"].set_visible(False)

    axis.grid(
        axis="y",
        alpha=0.2,
        linewidth=0.8
    )

    axis.set_axisbelow(True)


def save_figure(figure, filename):
    figure.tight_layout()

    figure.savefig(
        OUTPUT / filename,
        dpi=300,
        bbox_inches="tight"
    )

    plt.close(figure)


def print_results(results):
    for result in results:
        print()
        print(result["name"])

        print(
            f'Wins: {result["wins"]}/{result["games"]} '
            f'({result["win_rate"] * 100:.2f}%)'
        )

        print(
            "Wilson 95% CI: "
            f'{result["ci_low"] * 100:.2f}% to '
            f'{result["ci_high"] * 100:.2f}%'
        )

        print(
            "Exact binomial p vs 50%: "
            f'{result["p_value"]:.6g}'
        )

        print(
            f'Incomplete: {result["incomplete"]}'
        )

        print(
            f'Mean time: '
            f'{result["mean_time_ms"]:.3f} ms'
        )

        print(
            f'Median time: '
            f'{result["median_time_ms"]:.3f} ms'
        )

        print(
            f'95th percentile time: '
            f'{result["p95_time_ms"]:.3f} ms'
        )

        if result["mean_nodes"] is not None:
            print(
                f'Mean nodes: '
                f'{result["mean_nodes"]:.2f}'
            )

            print(
                f'Median nodes: '
                f'{result["median_nodes"]:.2f}'
            )

            print(
                f'95th percentile nodes: '
                f'{result["p95_nodes"]:.2f}'
            )

        if result["budget_hit_rate"] is not None:
            print(
                "Budget-hit decisions: "
                f'{result["budget_hit_rate"] * 100:.2f}%'
            )

        print(
            f'WHITE: {result["white"]["wins"]}/'
            f'{result["white"]["games"]} '
            f'({result["white"]["win_rate"] * 100:.2f}%)'
        )

        print(
            f'BLACK: {result["black"]["wins"]}/'
            f'{result["black"]["games"]} '
            f'({result["black"]["win_rate"] * 100:.2f}%)'
        )


def create_win_rate_figure(expectimax_results):
    labels = [
        result["name"]
        for result in expectimax_results
    ]

    win_rates = [
        result["win_rate"] * 100
        for result in expectimax_results
    ]

    lower_errors = [
        (
            result["win_rate"]
            - result["ci_low"]
        ) * 100
        for result in expectimax_results
    ]

    upper_errors = [
        (
            result["ci_high"]
            - result["win_rate"]
        ) * 100
        for result in expectimax_results
    ]

    figure, axis = plt.subplots(
        figsize=(9, 5.5)
    )

    prepare_axis(axis)

    axis.errorbar(
        labels,
        win_rates,
        yerr=[
            lower_errors,
            upper_errors
        ],
        fmt="o",
        markersize=7,
        capsize=5,
        linewidth=1.5
    )

    axis.axhline(
        50,
        linestyle="--",
        linewidth=1.2,
        alpha=0.7
    )

    axis.set_title(
        "Expectimax Win Rate Against Heuristic AI",
        pad=15
    )

    axis.set_ylabel(
        "Win Rate (%)"
    )

    axis.set_xlabel(
        "Expectimax Configuration",
        labelpad=10
    )

    axis.tick_params(
        axis="x",
        rotation=20
    )

    save_figure(
        figure,
        "figure_1_expectimax_win_rate.png"
    )


def create_node_figure(expectimax_results):
    labels = [
        result["name"]
        for result in expectimax_results
    ]

    means = [
        result["mean_nodes"]
        for result in expectimax_results
    ]

    medians = [
        result["median_nodes"]
        for result in expectimax_results
    ]

    p95_values = [
        result["p95_nodes"]
        for result in expectimax_results
    ]

    positions = list(
        range(len(labels))
    )

    figure, axis = plt.subplots(
        figsize=(9, 5.5)
    )

    prepare_axis(axis)

    axis.plot(
        positions,
        medians,
        marker="o",
        linestyle="",
        markersize=8,
        label="Median"
    )

    axis.plot(
        positions,
        means,
        marker="s",
        linestyle="",
        markersize=8,
        label="Mean"
    )

    axis.plot(
        positions,
        p95_values,
        marker="^",
        linestyle="",
        markersize=8,
        label="95th percentile"
    )

    axis.set_yscale(
        "log"
    )

    axis.set_title(
        "Expectimax Search Cost by Configuration",
        pad=15
    )

    axis.set_ylabel(
        "Nodes Evaluated per Decision (log scale)"
    )

    axis.set_xlabel(
        "Expectimax Configuration",
        labelpad=10
    )

    axis.set_xticks(
        positions,
        labels,
        rotation=20
    )

    axis.legend(
        frameon=False
    )

    save_figure(
        figure,
        "figure_2_expectimax_nodes.png"
    )


def create_time_figure(expectimax_results):
    labels = [
        result["name"]
        for result in expectimax_results
    ]

    means = [
        result["mean_time_ms"]
        for result in expectimax_results
    ]

    medians = [
        result["median_time_ms"]
        for result in expectimax_results
    ]

    p95_values = [
        result["p95_time_ms"]
        for result in expectimax_results
    ]

    positions = list(
        range(len(labels))
    )

    figure, axis = plt.subplots(
        figsize=(9, 5.5)
    )

    prepare_axis(axis)

    axis.plot(
        positions,
        medians,
        marker="o",
        linestyle="",
        markersize=8,
        label="Median"
    )

    axis.plot(
        positions,
        means,
        marker="s",
        linestyle="",
        markersize=8,
        label="Mean"
    )

    axis.plot(
        positions,
        p95_values,
        marker="^",
        linestyle="",
        markersize=8,
        label="95th percentile"
    )

    axis.set_yscale(
        "log"
    )

    axis.set_title(
        "Expectimax Decision Time by Configuration",
        pad=15
    )

    axis.set_ylabel(
        "Decision Time (ms, log scale)"
    )

    axis.set_xlabel(
        "Expectimax Configuration",
        labelpad=10
    )

    axis.set_xticks(
        positions,
        labels,
        rotation=20
    )

    axis.legend(
        frameon=False
    )

    save_figure(
        figure,
        "figure_3_expectimax_decision_time.png"
    )


def create_colour_figure(expectimax_results):
    labels = [
        result["name"]
        for result in expectimax_results
    ]

    white_rates = [
        result["white"]["win_rate"] * 100
        for result in expectimax_results
    ]

    black_rates = [
        result["black"]["win_rate"] * 100
        for result in expectimax_results
    ]

    positions = list(
        range(len(labels))
    )

    width = 0.34

    figure, axis = plt.subplots(
        figsize=(9, 5.5)
    )

    prepare_axis(axis)

    axis.bar(
        [
            position - width / 2
            for position in positions
        ],
        white_rates,
        width,
        label="White"
    )

    axis.bar(
        [
            position + width / 2
            for position in positions
        ],
        black_rates,
        width,
        label="Black"
    )

    axis.axhline(
        50,
        linestyle="--",
        linewidth=1.2,
        alpha=0.7
    )

    axis.set_title(
        "Expectimax Win Rate by Playing Colour",
        pad=15
    )

    axis.set_ylabel(
        "Win Rate (%)"
    )

    axis.set_xlabel(
        "Expectimax Configuration",
        labelpad=10
    )

    axis.set_xticks(
        positions,
        labels,
        rotation=20
    )

    axis.legend(
        frameon=False
    )

    save_figure(
        figure,
        "figure_4_expectimax_colour.png"
    )


def create_nodes_vs_time_figure(expectimax_results):
    pairs = []

    for result in expectimax_results:
        pairs.extend(
            result["node_time_pairs"]
        )

    nodes = [
        pair[0]
        for pair in pairs
    ]

    times = [
        pair[1]
        for pair in pairs
    ]

    log_nodes = [
        math.log10(value)
        for value in nodes
    ]

    log_times = [
        math.log10(value)
        for value in times
    ]

    regression = linregress(
        log_nodes,
        log_times
    )

    correlation = pearsonr(
        log_nodes,
        log_times
    )

    minimum_x = min(log_nodes)
    maximum_x = max(log_nodes)

    fitted_log_x = [
        minimum_x
        + (
            maximum_x - minimum_x
        ) * index / 199
        for index in range(200)
    ]

    fitted_log_y = [
        regression.intercept
        + regression.slope * x
        for x in fitted_log_x
    ]

    fitted_x = [
        10 ** value
        for value in fitted_log_x
    ]

    fitted_y = [
        10 ** value
        for value in fitted_log_y
    ]

    figure, axis = plt.subplots(
        figsize=(9, 5.5)
    )

    hexbin = axis.hexbin(
        nodes,
        times,
        gridsize=55,
        xscale="log",
        yscale="log",
        mincnt=1,
        bins="log"
    )

    colour_bar = figure.colorbar(
        hexbin,
        ax=axis
    )

    colour_bar.set_label(
        "Decision Density"
    )

    axis.plot(
        fitted_x,
        fitted_y,
        linewidth=2,
        label="Log-log regression"
    )

    axis.set_xscale(
        "log"
    )

    axis.set_yscale(
        "log"
    )

    axis.set_title(
        "Search Effort and Expectimax Decision Time",
        pad=15
    )

    axis.set_xlabel(
        "Nodes Evaluated per Decision (log scale)"
    )

    axis.set_ylabel(
        "Decision Time (ms, log scale)"
    )

    axis.text(
        0.03,
        0.96,
        (
            f"Pearson r = "
            f"{correlation.statistic:.3f}\n"
            f"R² = "
            f"{regression.rvalue ** 2:.3f}"
        ),
        transform=axis.transAxes,
        verticalalignment="top"
    )

    axis.legend(
        frameon=False,
        loc="lower right"
    )

    save_figure(
        figure,
        "figure_5_nodes_vs_time.png"
    )

    return (
        correlation.statistic,
        regression.rvalue ** 2,
        regression.pvalue
    )


def get_depth_two_results(results):
    names = (
        "Depth 2 / 1k",
        "Depth 2 / 5k",
        "Depth 2 / 10k"
    )

    return [
        next(
            result
            for result in results
            if result["name"] == name
        )
        for name in names
    ]


def create_budget_vs_cost_figure(depth_two_results):
    budgets = [
        1000,
        5000,
        10000
    ]

    median_times = [
        result["median_time_ms"]
        for result in depth_two_results
    ]

    figure, axis = plt.subplots(
        figsize=(8, 5)
    )

    prepare_axis(axis)

    axis.plot(
        budgets,
        median_times,
        marker="o",
        markersize=7,
        linewidth=2
    )

    axis.set_title(
        "Depth-2 Search Budget and Decision Cost",
        pad=15
    )

    axis.set_xlabel(
        "Configured Node Budget"
    )

    axis.set_ylabel(
        "Median Decision Time (ms)"
    )

    axis.set_xticks(
        budgets,
        ["1,000", "5,000", "10,000"]
    )

    save_figure(
        figure,
        "figure_6_budget_vs_cost.png"
    )


def create_budget_vs_win_rate_figure(
        depth_two_results):

    budgets = [
        1000,
        5000,
        10000
    ]

    win_rates = [
        result["win_rate"] * 100
        for result in depth_two_results
    ]

    lower_errors = [
        (
            result["win_rate"]
            - result["ci_low"]
        ) * 100
        for result in depth_two_results
    ]

    upper_errors = [
        (
            result["ci_high"]
            - result["win_rate"]
        ) * 100
        for result in depth_two_results
    ]

    figure, axis = plt.subplots(
        figsize=(8, 5)
    )

    prepare_axis(axis)

    axis.errorbar(
        budgets,
        win_rates,
        yerr=[
            lower_errors,
            upper_errors
        ],
        fmt="o-",
        markersize=7,
        capsize=5,
        linewidth=1.8
    )

    axis.axhline(
        50,
        linestyle="--",
        linewidth=1.2,
        alpha=0.7
    )

    axis.set_title(
        "Depth-2 Search Budget and Playing Strength",
        pad=15
    )

    axis.set_xlabel(
        "Configured Node Budget"
    )

    axis.set_ylabel(
        "Win Rate Against Heuristic AI (%)"
    )

    axis.set_xticks(
        budgets,
        ["1,000", "5,000", "10,000"]
    )

    save_figure(
        figure,
        "figure_7_budget_vs_win_rate.png"
    )


def export_summary_csv(results):
    output_path = (
        OUTPUT
        / "experiment_summary.csv"
    )

    with output_path.open(
            "w",
            newline="",
            encoding="utf-8") as file:

        writer = csv.writer(file)

        writer.writerow([
            "configuration",
            "wins",
            "games",
            "win_rate",
            "ci_low",
            "ci_high",
            "p_value",
            "mean_time_ms",
            "median_time_ms",
            "p95_time_ms",
            "mean_nodes",
            "median_nodes",
            "p95_nodes",
            "budget_hit_rate",
            "white_win_rate",
            "black_win_rate",
        ])

        for result in results:
            writer.writerow([
                result["name"],
                result["wins"],
                result["games"],
                result["win_rate"],
                result["ci_low"],
                result["ci_high"],
                result["p_value"],
                result["mean_time_ms"],
                result["median_time_ms"],
                result["p95_time_ms"],
                result["mean_nodes"],
                result["median_nodes"],
                result["p95_nodes"],
                result["budget_hit_rate"],
                result["white"]["win_rate"],
                result["black"]["win_rate"],
            ])


def main():
    results = [
        analyse_experiment(
            name,
            folder_name
        )
        for name, folder_name
        in EXPERIMENTS.items()
    ]

    print_results(
        results
    )

    expectimax_results = [
        result
        for result in results
        if result["target_ai"]
        == "EXPECTIMAX_AI"
    ]

    create_win_rate_figure(
        expectimax_results
    )

    create_node_figure(
        expectimax_results
    )

    create_time_figure(
        expectimax_results
    )

    create_colour_figure(
        expectimax_results
    )

    correlation, r_squared, regression_p = (
        create_nodes_vs_time_figure(
            expectimax_results
        )
    )

    depth_two_results = get_depth_two_results(
        results
    )

    create_budget_vs_cost_figure(
        depth_two_results
    )

    create_budget_vs_win_rate_figure(
        depth_two_results
    )

    export_summary_csv(
        results
    )

    bounded_depth_one = next(
        result
        for result in results
        if result["name"]
        == "Depth 1 / 10k"
    )

    untruncated_depth_one = next(
        result
        for result in results
        if result["name"]
        == "Depth 1 / untruncated"
    )

    z_score, p_value = compare_proportions(
        untruncated_depth_one,
        bounded_depth_one
    )

    print()
    print(
        "Depth 1 untruncated "
        "vs Depth 1 / 10k"
    )

    print(
        f"z = {z_score:.4f}"
    )

    print(
        f"p = {p_value:.4f}"
    )

    print()
    print(
        "Nodes evaluated "
        "vs decision time"
    )

    print(
        "Pearson r (log-log) = "
        f"{correlation:.4f}"
    )

    print(
        f"R^2 = {r_squared:.4f}"
    )

    if regression_p < 0.001:
        print(
            "Regression p < 0.001"
        )
    else:
        print(
            "Regression p = "
            f"{regression_p:.4f}"
        )

    print()
    print(
        "Analysis complete. "
        "Files saved to analysis-output/"
    )


if __name__ == "__main__":
    main()